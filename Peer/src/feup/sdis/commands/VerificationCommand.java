package feup.sdis.commands;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.initiators.HasChunkInitiator;
import feup.sdis.utils.Security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Verification command
 */
public class VerificationCommand implements Command {

    /**
     * Executes the verification command, which will check for zombie chunks and corrupted chunks.
     *
     * @return true if chunks were verified successfully
     */
    public static boolean execute() {
        final File f = new File("data");
        final File[] folders = f.listFiles();
        if (folders == null)
            return true;
        File[] chunks;

        // Check all chunks
        for (final File folder : folders) {
            chunks = folder.listFiles();
            if (chunks == null)
                continue;
            for (final File chunk : chunks) {
                // File UUID
                final UUID fileId = UUID.fromString(folder.getName());

                // Get chunk number
                String chunkFilename = chunk.getName();
                int pos = chunkFilename.lastIndexOf(".");
                if (pos > 0)
                    chunkFilename = chunkFilename.substring(0, pos);
                final int chunkNo = Integer.parseInt(chunkFilename);

                // Validate the checksum
                final byte[] body;
                final byte[] buffer = new byte[Protocol.CHUNK_SIZE * 2];
                final int size;
                final long checksum;
                try {
                    final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(chunk));
                    checksum = dataInputStream.readLong();
                    size = dataInputStream.read(buffer);
                    body = Arrays.copyOf(buffer, size);
                    dataInputStream.close();
                } catch (IOException e) {
                    Node.getLogger().log(Level.FATAL, "Could not read the chunk number " + chunkNo + " of the file " + fileId + ". " + e.getMessage());
                    continue;
                }
                final long currentChecksum = Security.checksum(body);
                if(currentChecksum != checksum) {
                    chunk.delete();
                    continue;
                }

                // Request the server to check if chunk is supposed to exist
                final HasChunkInitiator hasChunkInitiator = new HasChunkInitiator(Peer.getInstance().getMonitor(), fileId, chunkNo);
                hasChunkInitiator.setMaxRounds(100);
                final Thread hasChunkThread = new Thread(hasChunkInitiator);
                hasChunkThread.start();
                while (hasChunkThread.isAlive())
                    try {
                        hasChunkThread.join();
                    } catch (InterruptedException ignored) {
                    }

                // The file doesn't exist in the system, delete it
                if (hasChunkInitiator.hasReceivedResponse())
                    continue;
                chunk.delete();
            }

            chunks = folder.listFiles();
            if (chunks != null && chunks.length == 0)
                folder.delete();
        }

        return true;
    }
}