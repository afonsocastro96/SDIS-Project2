package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Removed message
 */
public class RemovedMessage extends ProtocolMessage {

    /**
     * Constructor of RemovedMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public RemovedMessage(final UUID fileId, final int chunkNo) {
        super(Type.REMOVED, Protocol.VERSION, fileId, chunkNo);
    }
}
