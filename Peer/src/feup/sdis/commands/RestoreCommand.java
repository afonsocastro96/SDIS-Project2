package feup.sdis.commands;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.initiators.GetChunkInitiator;
import feup.sdis.protocol.initiators.RestoreInitiator;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Restore command
 */
public class RestoreCommand implements Command {

    /**
     * Execute the restore command
     *
     * @param f file to be restored
     * @return true if was executed successful, false otherwise
     */
    public static boolean execute(final File f) {
        // Request the number of chunks of the file
        final RestoreInitiator restoreInitiator = new RestoreInitiator(Peer.getInstance().getMonitor(), f.getAbsolutePath());
        final Thread restoreThread = new Thread(restoreInitiator);
        restoreThread.start();
        while (restoreThread.isAlive())
            try {
                restoreThread.join();
            } catch (InterruptedException ignored) {
            }
        if (!restoreInitiator.hasReceivedResponse())
            return false;

        final ProtocolMessage message = restoreInitiator.getResponse();
        final int totalChunks = message.getChunkNo();
        if (totalChunks == -1)
            return false;
        final UUID fileId = message.getFileId();

        // Create and open the file
        final RandomAccessFile file;
        try {
            if (f.exists())
                if (!f.delete())
                    return false;
            if (!f.createNewFile())
                return false;
            file = new RandomAccessFile(f, "rw");
        } catch (Exception e) {
            Node.getLogger().log(Level.FATAL, "Could not create the file. " + e.getMessage());
            return false;
        }

        // Create needed variables
        byte[] buffer;
        GetChunkInitiator getChunkInitiator;
        Thread getChunkThread;

        // Get all the chunks
        Node.getLogger().log(Level.DEBUG, "Trying to restore " + totalChunks + " chunks.");
        for (int chunkNo = 0; chunkNo < totalChunks; chunkNo++) {
            getChunkInitiator = new GetChunkInitiator(Peer.getInstance().getMonitor(), fileId, chunkNo);
            getChunkInitiator.setMaxRounds(1000);
            getChunkThread = new Thread(getChunkInitiator);
            getChunkThread.start();

            while (getChunkThread.isAlive())
                try {
                    getChunkThread.join();
                } catch (InterruptedException ignored) {
                }
            if (!getChunkInitiator.hasReceivedResponse()) {
                try {
                    file.close();
                } catch (IOException ignored) {
                }
                f.delete();
                return false;
            }

            buffer = getChunkInitiator.getResponse().getBody();

            try {
                file.seek(chunkNo * Protocol.CHUNK_SIZE);
                file.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                try {
                    file.close();
                } catch (IOException ignored) {
                }
                f.delete();
                Node.getLogger().log(Level.FATAL, "Could not write the chunk number " + chunkNo + " of the file " + fileId + ". " + e.getMessage());
                return false;
            }
        }
        try {
            file.close();
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the file.");
        }

        return true;
    }

}
