package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Replicas total message
 */
public class StoredTotalMessage extends ProtocolMessage {

    /**
     * Replicas of a given chunk
     */
    final int replicas;

    /**
     * Constructor of ChunkTotalMessage
     * @param fileId file id to get the total number of chunks
     * @param chunkNo chunk number
     * @param replicas replicas of this chunk
     */
    public StoredTotalMessage(final UUID fileId, final int chunkNo, final int replicas) {
        super(Type.CHUNKTOTAL, Protocol.VERSION, fileId, chunkNo);
        this.replicas = replicas;
    }

    /**
     * Get the replicas that were stored
     * @return replicas that were stored
     */
    public int getReplicas() {
        return replicas;
    }
}

