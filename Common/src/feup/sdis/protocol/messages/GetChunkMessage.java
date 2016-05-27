package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Get chunk message
 */
public class GetChunkMessage extends ProtocolMessage {

    /**
     * Constructor of GetChunkMessage
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public GetChunkMessage(final UUID fileId, final int chunkNo) {
        super(Type.GETCHUNK, Protocol.VERSION, fileId, chunkNo);
    }
}
