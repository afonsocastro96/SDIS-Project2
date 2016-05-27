package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Put chunk message
 */
public class PutChunkMessage extends ProtocolMessage {

    /**
     * Constructor of PutChunkMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param minReplicas minimum replicas
     * @param body body of the chunk
     */
    public PutChunkMessage(final UUID fileId, final int chunkNo, final int minReplicas, byte[] body) {
        super(Type.PUTCHUNK, Protocol.VERSION, fileId, chunkNo, minReplicas, body);
    }
}
