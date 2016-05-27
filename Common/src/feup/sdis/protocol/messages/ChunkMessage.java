package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Chunk message
 */
public class ChunkMessage extends ProtocolMessage {

    /**
     * Constructor of ChunkMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param body body of the chunk
     */
    public ChunkMessage(final UUID fileId, final int chunkNo, byte[] body) {
        super(Type.CHUNK, Protocol.VERSION, fileId, chunkNo, body);
    }
}
