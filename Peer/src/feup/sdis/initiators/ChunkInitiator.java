package feup.sdis.initiators;

import feup.sdis.Peer;
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
     * @param chunkNo number of the chunk
     * @param body body of the chunk
     */
    public ChunkInitiator(int chunkNo, byte[] body){
        this.chunkNo = chunkNo;
        this.body = body;

        message = new ChunkMessage(Peer.getInstance().getId(), chunkNo, body);
    }
}
