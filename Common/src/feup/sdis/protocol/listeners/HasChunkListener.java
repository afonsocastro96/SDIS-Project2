package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.HasChunkParser;

import java.util.Observable;

/**
 * Created by Afonso on 25/05/2016.
 */
public class HasChunkListener extends ProtocolListener {
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;
        if(!(arg instanceof byte[]))
            return;

        // Validate message
        final ProtocolMessage message;
        try {
            message = new HasChunkParser().parse((byte[]) arg);
            Node.getLogger().log(Level.DEBUG, message.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse HASCHUNK message. " + e.getMessage());
            return;
        }
    }
}
