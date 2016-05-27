package feup.sdis.initiators;

import feup.sdis.Peer;
import feup.sdis.protocol.listeners.ChunkListener;
import feup.sdis.protocol.messages.GetChunkMessage;

import java.util.UUID;

/**
 * Get chunk initiator
 */
public class GetChunkInitiator extends ProtocolInitiator {

    /**
     * UUID of the file to get the chunk
     */
    private final UUID fileId;

    /**
     * Number of the chunk to get
     */
    private final int chunkNo;

    /**
     * Constructor of GetChunkInitiator
     * @param fileId id of the file to get the chunk
     * @param chunkNo number of the chunk to get
     */
    public GetChunkInitiator(UUID fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;

        message = new GetChunkMessage(fileId, chunkNo);
        listener = new ChunkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                fileId,
                chunkNo);
    }
}
