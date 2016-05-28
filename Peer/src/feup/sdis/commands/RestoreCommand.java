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
 * Restore command
 */
public class RestoreCommand implements Command {

    /**
     * Execute the restore command
     * @param f file to be restored
     * @return true if was executed successful, false otherwise
     */
    public static boolean execute(final File f) {
        // Request the number of chunks of the file
        final RestoreInitiator restoreInitiator = new RestoreInitiator(f.getAbsolutePath());
        final Thread restoreThread = new Thread(restoreInitiator);
        restoreThread.start();
        while(restoreThread.isAlive())
            try {
                restoreThread.join();
            } catch (InterruptedException ignored) {}
        if (!restoreInitiator.hasReceivedResponse())
            return false;

        final ProtocolMessage message = restoreInitiator.getResponse();
        final int totalChunks = message.getChunkNo();
        final UUID fileId = message.getFileId();
        if(totalChunks == -1)
            return false;

        // Create and open the file
        final RandomAccessFile file;
        try {
            if (!f.createNewFile())
                return false;
            file = new RandomAccessFile(f, "w");
        } catch (Exception e) {
            Node.getLogger().log(Level.FATAL, "Could not open the file. " + e.getMessage());
            return false;
        }

        // Create needed variables
        byte[] buffer;
        GetChunkInitiator getChunkInitiator;
        Thread getChunkThread;

        // Get all the chunks
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
