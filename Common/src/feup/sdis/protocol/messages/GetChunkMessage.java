package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Get chunk message
 */
public class GetChunkMessage extends ProtocolMessage {

    /**
     * Constructor of GetChunkMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public GetChunkMessage(final UUID senderId, final UUID fileId, final int chunkNo) {
        super(Type.GETCHUNK, Protocol.VERSION, senderId, fileId, chunkNo);
    }
}
