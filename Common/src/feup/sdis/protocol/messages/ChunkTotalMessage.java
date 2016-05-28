package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Chunk total message
 */
public class ChunkTotalMessage extends ProtocolMessage {

    /**
     * Constructor of ChunkTotalMessage
     * @param fileId file id to get the total number of chunks
     * @param chunkNo chunk number
     */
    public ChunkTotalMessage(final UUID fileId, final int chunkNo) {
        super(Type.CHUNKTOTAL, Protocol.VERSION, fileId, chunkNo);
    }
}

