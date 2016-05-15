package feup.sdis.protocol.messages;

import feup.sdis.protocol.listeners.ProtocolListener;

import java.util.UUID;

/**
 * Chunk message
 */
public class ChunkMessage extends ProtocolMessage {

    /**
     * Constructor of ChunkMessage
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param body body of the chunk
     */
    public ChunkMessage(final UUID senderId, final UUID fileId, final int chunkNo, byte[] body) {
        super(Type.CHUNK, ProtocolListener.VERSION, senderId, fileId, chunkNo, body);
    }
}
