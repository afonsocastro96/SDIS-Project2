package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Delete message
 */
public class DeleteMessage extends ProtocolMessage {

    /**
     * Constructor of DeleteMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     */
    public DeleteMessage(final UUID senderId, final UUID fileId) {
        super(Type.DELETE, Protocol.VERSION, senderId, fileId);
    }
}
