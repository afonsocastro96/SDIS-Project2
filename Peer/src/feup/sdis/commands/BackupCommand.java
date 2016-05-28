package feup.sdis.commands;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.initiators.FileNameInitiator;
import feup.sdis.protocol.initiators.PutChunkInitiator;
import feup.sdis.protocol.messages.StoredTotalMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.UUID;

/**
 * Backup command
 */
public class BackupCommand implements Command {

    /**
     * Execute the backup command
     *
     * @param f file to be backed up
     * @return true if file was backed up successfully, false otherwise
     */
    public static boolean execute(final File f, final int minReplicas) {
        // Send file name
        final FileNameInitiator fileNameInitiator = new FileNameInitiator(Peer.getInstance().getMonitor(), UUID.randomUUID(), f.getAbsolutePath());
        final Thread fileNameThread = new Thread(fileNameInitiator);
        fileNameThread.start();
        while (fileNameThread.isAlive())
            try {
                fileNameThread.join();
            } catch (InterruptedException ignored) {}
        if (!fileNameInitiator.hasReceivedResponse())
            return false;

        // Open file as random access
        final RandomAccessFile file;
        try {
            file = new RandomAccessFile(f, "r");
        } catch (FileNotFoundException e) {
            Node.getLogger().log(Level.FATAL, "Could not open the file. " + e.getMessage());
            return false;
        }

        // Create needed variables
        byte[] buffer = new byte[Protocol.CHUNK_SIZE];
        int totalChunks = (int) (f.length() / Protocol.CHUNK_SIZE) + 1;
        PutChunkInitiator putChunkInitiator;
        Thread putChunkThread;
        int replicationDegree;

        // Send all chunks
        for (int chunkNo = 0; chunkNo < totalChunks; ++chunkNo) {
            try {
                file.seek(chunkNo * Protocol.CHUNK_SIZE);
                int size = file.read(buffer);

                // Send the chunk
                putChunkInitiator = new PutChunkInitiator(Peer.getInstance().getMonitor(), fileNameInitiator.getFileId(), chunkNo, minReplicas, Arrays.copyOf(buffer, size));
                putChunkInitiator.setMaxRounds(minReplicas * 3000 * 2); // Wait at least 3 seconds for each peer times minimum replicas
                putChunkThread = new Thread(putChunkInitiator);
                putChunkThread.start();
                while (putChunkThread.isAlive())
                    try {
                        putChunkThread.join();
                    } catch (InterruptedException ignored) {}
                if (!putChunkInitiator.hasReceivedResponse())
                    return false;

                replicationDegree = ((StoredTotalMessage) putChunkInitiator.getResponse()).getReplicas();
                if(replicationDegree == 0)
                    Node.getLogger().log(Level.ERROR, "Could not backup the chunk number " + chunkNo + ".");
                else if(replicationDegree < minReplicas)
                    Node.getLogger().log(Level.WARNING, "Could not get the desired replication degree (" + replicationDegree + " / " + minReplicas + ") of the chunk number " + chunkNo + ".");
            } catch (IOException e) {
                Node.getLogger().log(Level.FATAL, "Could not seek the specified chunk number " + chunkNo + ". " + e.getMessage());
                return false;
            }
        }
        try {
            file.close();
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the file " + f.getName() + ".");
        }

        return true;
    }

}
