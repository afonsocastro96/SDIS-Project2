package feup.sdis.commands;

import feup.sdis.Node;
import feup.sdis.initiators.GetChunkInitiator;
import feup.sdis.initiators.RestoreInitiator;
import feup.sdis.logger.Level;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by Afonso on 27/05/2016.
 */
public class RestoreCommand implements Command {
    public static boolean execute(final File f) {
        /* Request the number of chunks */
        final RestoreInitiator restoreInitiator = new RestoreInitiator(f.getAbsolutePath());
        final Thread restoreThread = new Thread(restoreInitiator);
        restoreThread.start();
        while(restoreThread.isAlive())
            try {
                restoreThread.join();
            } catch (InterruptedException ignored) {}
        if (!restoreInitiator.hasReceivedResponse())
            return false;

        ProtocolMessage message = restoreInitiator.getResponse();
        int totalChunks = message.getChunkNo();
        UUID fileId = message.getFileId();
        if(totalChunks == -1)
            return false;

        /* Create the file and open it*/
        final RandomAccessFile file;
        try {
            if (!f.createNewFile())
                return false;
            file = new RandomAccessFile(f, "w");
        } catch (FileNotFoundException e) {
            Node.getLogger().log(Level.FATAL, "Could not open the file. " + e.getMessage());
            return false;
        } catch (IOException e) {
            return false;
        }

        byte[] buffer;
        GetChunkInitiator getChunkInitiator;
        Thread getChunkThread;

        /* Get the chunks */
        for(int chunkNo = 0; chunkNo < totalChunks; ++chunkNo){
            getChunkInitiator = new GetChunkInitiator(fileId, chunkNo);
            getChunkThread = new Thread(getChunkInitiator);
            getChunkThread.start();

            while (getChunkThread.isAlive())
                try {
                    getChunkThread.join();
                } catch (InterruptedException ignored) {}

            if(!getChunkInitiator.hasReceivedResponse())
                return false;

            buffer = getChunkInitiator.getResponse().getBody();

            try{
                file.seek(chunkNo * Protocol.CHUNK_SIZE);
                file.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                Node.getLogger().log(Level.FATAL, "Could not seek the specified chunk. " + e.getMessage());
                return false;
            }
        }

        return true;
    }

}
