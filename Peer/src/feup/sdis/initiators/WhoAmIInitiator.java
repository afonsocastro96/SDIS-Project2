package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.WhoAmIMessage;

import java.io.IOException;

/**
 * Who am I initiator
 */
public class WhoAmIInitiator extends ProtocolInitiator {

    /**
     * Send the UUID of the peer to the server
     */
    @Override
    public void run() {
        // Send UUID message
        final WhoAmIMessage message = new WhoAmIMessage(Peer.getInstance().getId());
        try {
            Peer.getInstance().getMonitor().write(message.getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
            return;
        }

        final OkListener listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
        Peer.getInstance().getMonitor().addObserver(listener);

        // Wait for the Ok response
        while(!listener.hasReceivedResponse()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        Peer.getInstance().getMonitor().deleteObserver(listener);
        Node.getLogger().log(Level.DEBUG, "Server has received our UUID.");
    }
}
