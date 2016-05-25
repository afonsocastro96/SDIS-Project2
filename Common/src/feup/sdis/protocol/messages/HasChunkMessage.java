package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Created by Afonso on 25/05/2016.
 */
public class HasChunkMessage extends ProtocolMessage {
    public HasChunkMessage(UUID senderId, UUID fileId) {
        super(Type.HASCHUNK, Protocol.VERSION, senderId, fileId);
    }
}
