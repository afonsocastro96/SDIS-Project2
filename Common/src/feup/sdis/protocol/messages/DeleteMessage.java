package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Delete message
 */
public class DeleteMessage extends ProtocolMessage {

    /**
     * Constructor of DeleteMessage
     * @param fileId id of the file
     */
    public DeleteMessage(final UUID fileId) {
        super(Type.DELETE, Protocol.VERSION, fileId);
    }
}
