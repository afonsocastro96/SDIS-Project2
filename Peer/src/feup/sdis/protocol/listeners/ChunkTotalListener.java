package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkTotalMessage;
import feup.sdis.protocol.messages.parsers.ChunkTotalParser;

import java.util.Observable;

/**
 * Chunk total listener
 */
public class ChunkTotalListener extends ProtocolListener {

    /**
     * Host where the chunk total should come
     */
    private final String host;

    /**
     * Port where the chunk total should come
     */
    private final int port;

    /**
     * File name to get the total number of chunks
     */
    private final String fileName;

    /**
     * Constructor of ChunkTotalListener
     * @param host host from where the chunk total should come
     * @param port port from where the chunk total should come
     * @param fileName file name to get the total number of chunks
     */
    public ChunkTotalListener(final String host, final int port, final String fileName) {
        this.receivedResponse = false;
        this.host = host;
        this.port = port;
        this.fileName = fileName;
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
            protocolMessage = new ChunkTotalParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse CHUNKTOTAL message. " + e.getMessage());
            return;
        }

        // Check if this is the chunk total we are expecting
        if(!host.equalsIgnoreCase(this.host))
            return;
        if(port != this.port)
            return;
        if(!((ChunkTotalMessage)protocolMessage).getFileName().equalsIgnoreCase(fileName))
            return;

        this.receivedResponse = true;
    }
}
