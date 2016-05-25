package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.OkParser;

import java.util.Observable;

/**
 * Created by Afonso on 25/05/2016.
 */
public class OkListener extends ProtocolListener {
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;

        if(!(arg instanceof byte[]))
            return;

        // Validate message
        final ProtocolMessage message;
        try {
            message = new OkParser().parse((byte[]) arg);
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse OK message. " + e.getMessage());
            return;
        }
    }
}
