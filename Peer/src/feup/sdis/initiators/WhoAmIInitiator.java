package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.listeners.ProtocolListener;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.WhoAmIMessage;

import java.io.IOException;

/**
 * Who am I initiator
 */
public class WhoAmIInitiator extends ProtocolInitiator {

    /**
     * Constructor of WhoAmIInitiator
     */
    public WhoAmIInitiator() {
        message = new WhoAmIMessage(Peer.getInstance().getId());
        listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
    }

    /**
     * Send the UUID of the peer to the server
     */
    @Override
    public void run() {
        Peer.getInstance().getMonitor().addObserver(listener);

        while(!listener.hasReceivedResponse()) {
            // Maximum attempts
            if(getAttempts() >= MAX_ATTEMPTS)
                break;

            // Send the message
            if(getRounds() == 0) {
                sendMessage(message);
                increaseAttempts();
            }

            // Increase number of rounds
            increaseRounds();
            if(getRounds() >= MAX_ROUNDS)
                resetRounds();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        Peer.getInstance().getMonitor().deleteObserver(listener);

        if(listener.hasReceivedResponse())
            Node.getLogger().log(Level.DEBUG, "Server has acknowledged our ID.");
        else
            Node.getLogger().log(Level.FATAL, "Could not send our ID to the server.");
    }
}
