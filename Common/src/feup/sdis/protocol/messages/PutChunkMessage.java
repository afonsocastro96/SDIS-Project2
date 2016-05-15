package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Put chunk message
 */
public class PutChunkMessage extends ProtocolMessage {

    /**
     * Constructor of PutChunkMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param replicationDeg replication degree
     * @param body body of the chunk
     */
    public PutChunkMessage(final UUID senderId, final UUID fileId, final int chunkNo, final int replicationDeg, byte[] body) {
        super(Type.PUTCHUNK, Protocol.VERSION, senderId, fileId, chunkNo, replicationDeg, body);
    }
}
