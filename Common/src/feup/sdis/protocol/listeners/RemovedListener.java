package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.RemovedParser;

import java.util.Observable;

/**
 * Removed listener
 */
public class RemovedListener extends ProtocolListener {

    /**
     * Called when a new message is received
     * @param o object that was being observed
     * @param arg argument of the notification
     */
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;

        if(!(arg instanceof byte[]))
            return;

        // Validate message
        final ProtocolMessage message;
        try {
            message = new RemovedParser().parse((byte[]) arg);
        } catch (MalformedMessageException e) {
            return;
        }

        Node.getLogger().log(Level.DEBUG, "Received a message " + message.getHeader() + " with body size of " + message.getBody().length);
    }
}
