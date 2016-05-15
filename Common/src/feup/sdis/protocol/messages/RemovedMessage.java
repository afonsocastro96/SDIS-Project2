package feup.sdis.protocol.messages;

import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Removed message
 */
public class RemovedMessage extends ProtocolMessage {

    /**
     * Constructor of RemovedMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public RemovedMessage(final UUID senderId, final UUID fileId, final int chunkNo) {
        super(Type.REMOVED, ProtocolListener.VERSION, senderId, fileId, chunkNo);
    }
}
