package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.StoredTotalListener;
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
     * @param monitor monitor of this channel
     * @param chunkNo number of the chunk to put
     * @param minReplicas replication degree
     * @param body
     */
    public PutChunkInitiator(final SSLManager monitor, final UUID fileId, final int chunkNo, final int minReplicas, final byte[] body){
        super(monitor);
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.minReplicas = minReplicas;
        this.body = body;

        message = new PutChunkMessage(this.fileId, this.chunkNo, this.minReplicas, this.body);
        listener = new StoredTotalListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                this.fileId,
                this.chunkNo);
    }
}
