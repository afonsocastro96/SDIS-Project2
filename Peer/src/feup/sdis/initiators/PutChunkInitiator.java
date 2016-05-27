package feup.sdis.initiators;

import feup.sdis.Peer;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.PutChunkMessage;

import java.util.UUID;

/**
 * Put chunk initiator
 */
public class PutChunkInitiator extends ProtocolInitiator {

    /**
     * Id of the file of the chunk
     */
    private final UUID fileId;

    /**
     * Number of the chunk to send
     */
    private final int chunkNo;

    /**
     * Minimum replicas of the chunk
     */
    private final int minReplicas;

    /**
     * Body of the chunk
     */
    private final byte[] body;

    /**
     * Constructor of PutChunkInitiator
     * @param chunkNo number of the chunk to put
     * @param minReplicas replication degree
     * @param body
     */
    public PutChunkInitiator(final UUID fileId, final int chunkNo, final int minReplicas, final byte[] body){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.minReplicas = minReplicas;
        this.body = body;

        message = new PutChunkMessage(fileId, chunkNo, minReplicas, body);
        listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
    }
}
