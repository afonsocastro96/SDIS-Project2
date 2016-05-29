package feup.sdis.commands;

import feup.sdis.Peer;
import feup.sdis.protocol.initiators.HasChunkInitiator;

import java.io.File;
import java.util.UUID;

/**
 * Verification command
 */
public class VerificationCommand implements Command {

    /**
     * Executes the verification command, which will check for zombie chunks and corrupted chunks.
     */
    public static void execute(){
        final File f = new File("data");
        final File[] folders = f.listFiles();
        if(folders == null)
            return;
        for(final File folder : folders){
            final HasChunkInitiator hasChunkInitiator = new HasChunkInitiator(Peer.getInstance().getMonitor(), UUID.fromString(folder.getName()));
            final Thread hasChunkThread = new Thread(hasChunkInitiator);
            hasChunkThread.start();
            while (hasChunkThread.isAlive())
                try {
                    hasChunkThread.join();
                } catch (InterruptedException ignored) {}

            // The file doesn't exist in the system, delete it
            if (!hasChunkInitiator.hasReceivedResponse())
                if(!folder.delete())
                    return;
        }
    }
}
