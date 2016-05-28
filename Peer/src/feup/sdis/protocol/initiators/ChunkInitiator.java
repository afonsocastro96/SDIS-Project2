package feup.sdis.protocol.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.messages.ChunkMessage;

/**
 * Chunk initiator
 */
public class ChunkInitiator extends ProtocolInitiator {

    /**
     * Number of the chunk
     */
    private final int chunkNo;

    /**
     * Body of the chunk
     */
    private final byte[] body;

    /**
     * Constructor of ChunkInitiator
     * @param monitor monitor of the initiator
     * @param chunkNo number of the chunk
     * @param body body of the chunk
     */
    public ChunkInitiator(final SSLManager monitor, final int chunkNo, final byte[] body){
        super(monitor);
        this.chunkNo = chunkNo;
        this.body = body;

        message = new ChunkMessage(Peer.getInstance().getId(), chunkNo, body);
    }

    /**
     * Send the chunk
     */
    @Override
    public void run() {
        if(sendMessage(message))
            Node.getLogger().log(Level.DEBUG, "Server received the message " + message.getHeader());
        else
            Node.getLogger().log(Level.ERROR, "Could not send the message " + message.getHeader() + " to the server.");
    }
}
