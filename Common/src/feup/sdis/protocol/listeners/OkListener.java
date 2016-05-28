package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.OkParser;

import java.util.Observable;

/**
 * Ok listener
 */
public class OkListener extends ProtocolListener {

    /**
     * Host where the Ok should come
     */
    private final String host;

    /**
     * Port where the Ok should come
     */
    private final int port;

    /**
     * Message corresponding to the Ok response
     */
    private final String message;

    /**
     * Constructor of OkListener
     * @param host host from where the Ok should come
     * @param port port from where the Ok should come
     * @param message message corresponding to the Ok response
     */
    public OkListener(final String host, final int port, final String message) {
        this.receivedResponse = false;
        this.host = host;
        this.port = port;
        this.message = message;
    }

    /**
     * Called when a new message is received
     * @param o object that was being observed
     * @param arg argument of the notification
     */
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;
        if(!(arg instanceof Object[]))
            return;

        // Validate data type of the objects
        final Object[] objects = (Object[]) arg;
        if(!(objects[0] instanceof String))
            return;
        if(!(objects[1] instanceof Integer))
            return;
        if(!(objects[2] instanceof byte[]))
            return;

        final String host = (String) objects[0];
        final int port = (Integer) objects[1];
        final byte[] message = (byte[]) objects[2];

        // Validate message
        try {
            protocolMessage = new OkParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse OK message. " + e.getMessage());
            return;
        }

        // Check if this is the Ok we are expecting
        if(!host.equalsIgnoreCase(this.host))
            return;
        if(port != this.port)
            return;
        if(!((OkMessage) protocolMessage).getMessage().equalsIgnoreCase(this.message))
            return;

        receivedResponse = true;
    }
}
