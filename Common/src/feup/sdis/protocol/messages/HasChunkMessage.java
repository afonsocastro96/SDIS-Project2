package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Has chunk message
 */
public class HasChunkMessage extends ProtocolMessage {

    /**
     * Constructor of HasChunkMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public HasChunkMessage(final UUID fileId, final int chunkNo) {
        super(Type.HASCHUNK, Protocol.VERSION, fileId, chunkNo);
    }
}
