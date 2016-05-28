package feup.sdis.commands;

import feup.sdis.Node;
import feup.sdis.initiators.FileNameInitiator;
import feup.sdis.initiators.PutChunkInitiator;
import feup.sdis.logger.Level;
import feup.sdis.protocol.Protocol;

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
        final FileNameInitiator fileNameInitiator = new FileNameInitiator(UUID.randomUUID(), f.getAbsolutePath());
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

        // Send all chunks
        for (int chunkNo = 0; chunkNo < totalChunks; ++chunkNo) {
            try {
                file.seek(chunkNo * Protocol.CHUNK_SIZE);
                int size = file.read(buffer);

                // Send the chunk
                putChunkInitiator = new PutChunkInitiator(fileNameInitiator.getFileId(), chunkNo, minReplicas, Arrays.copyOf(buffer, size));
                putChunkThread = new Thread(putChunkInitiator);
                putChunkThread.start();
                while (putChunkThread.isAlive())
                    try {
                        putChunkThread.join();
                    } catch (InterruptedException ignored) {}
                if (!putChunkInitiator.hasReceivedResponse())
                    return false;
            } catch (IOException e) {
                Node.getLogger().log(Level.FATAL, "Could not seek the specified chunk. " + e.getMessage());
                return false;
            }
        }

        return true;
    }

}
