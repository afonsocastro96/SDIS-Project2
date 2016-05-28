package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Chunk total message
 */
public class ChunkTotalMessage extends ProtocolMessage {

    /**
     * Constructor of ChunkTotalMessage
     * @param fileId file id to get the total number of chunks
     * @param numberChunks number of chunks
     */
    public ChunkTotalMessage(final UUID fileId, final int numberChunks) {
        super(Type.CHUNKTOTAL, Protocol.VERSION, fileId, numberChunks);
    }
}

