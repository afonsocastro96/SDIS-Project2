package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
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
        super(Type.STOREDTOTAL, Protocol.VERSION, fileId, chunkNo);
        this.replicas = replicas;
    }

    /**
     * Get the replicas that were stored
     * @return replicas that were stored
     */
    public int getReplicas() {
        return replicas;
    }

    /**c
     * Get the header of the message
     * @return header of the message
     */
    public String getHeader() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(getMessageType().toString())
                .add("" + getVersion())
                .add("" + (getFileId() != null ? getFileId() : ""))
                .add("" + (getChunkNo() >= 0 ? getChunkNo() : ""))
                .add("" + (getReplicas() >= 0 ? getReplicas() : ""));

        return sj.toString();
    }
}

