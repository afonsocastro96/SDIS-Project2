package feup.sdis.commands;

import feup.sdis.Peer;
import feup.sdis.protocol.initiators.DeleteInitiator;

import java.io.File;

/**
 * Delete command
 */
public class DeleteCommand implements Command {

    /**
     * Execute the delete command
     *
     * @param f file to be restored
     * @return true if was executed successful, false otherwise
     */
    public static boolean execute(final File f) {
        final DeleteInitiator deleteInitiator = new DeleteInitiator(Peer.getInstance().getMonitor(), f.getAbsolutePath());
        final Thread deleteThread = new Thread(deleteInitiator);
        deleteThread.start();
        while (deleteThread.isAlive())
            try {
                deleteThread.join();
            } catch (InterruptedException ignored) {
            }
        return deleteInitiator.hasReceivedResponse();
    }

}
