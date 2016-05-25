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
     */
    public HasChunkMessage(final UUID fileId) {
        super(Type.HASCHUNK, Protocol.VERSION, fileId);
    }
}
