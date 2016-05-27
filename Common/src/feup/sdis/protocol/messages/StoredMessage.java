package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Stored message
 */
public class StoredMessage extends ProtocolMessage {

    /**
     * Constructor of StoredMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public StoredMessage(final UUID fileId, final int chunkNo) {
        super(Type.STORED, Protocol.VERSION, fileId, chunkNo);
    }
}
