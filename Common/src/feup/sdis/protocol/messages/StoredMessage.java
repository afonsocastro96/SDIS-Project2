package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Stored message
 */
public class StoredMessage extends ProtocolMessage {

    /**
     * Constructor of StoredMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public StoredMessage(final UUID senderId, final UUID fileId, final int chunkNo) {
        super(Type.STORED, Protocol.VERSION, senderId, fileId, chunkNo);
    }
}
