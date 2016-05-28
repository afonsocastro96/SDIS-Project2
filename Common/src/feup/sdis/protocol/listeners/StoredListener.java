package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.parsers.StoredParser;

import java.util.Observable;
import java.util.UUID;

/**
 * Stored listener
 */
public class StoredListener extends ProtocolListener {

    /**
     * Host where the stored should come
     */
    private final String host;

    /**
     * Port where the stored should come
     */
    private final int port;

    /**
     * File id that was stored
     */
    private final UUID fileId;

    /**
     * Chunk number that was stored
     */
    private final int chunkNo;

    /**
     * Constructor of StoredListener
     * @param host host from where the stored should come
     * @param port port from where the stored should come
     * @param fileId file id that was stored
     * @param chunkNo number of the chunk that was stored
     */
    public StoredListener(final String host, final int port, final UUID fileId, final int chunkNo) {
        this.receivedResponse = false;
        this.host = host;
        this.port = port;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
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
            protocolMessage = new StoredParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse STORED message. " + e.getMessage());
            return;
        }

        // Check if this is the stored we are expecting
        if(!host.equalsIgnoreCase(this.host))
            return;
        if(port != this.port)
            return;
        if(protocolMessage.getFileId() != fileId)
            return;
        if(protocolMessage.getChunkNo() != chunkNo)
            return;

        receivedResponse = true;
    }
}
