package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
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
     * @param monitor monitor of this channel
     * @param fileId id of the file to get the chunk
     * @param chunkNo number of the chunk to get
     */
    public GetChunkInitiator(final SSLManager monitor, final UUID fileId, final int chunkNo){
        super(monitor);
        this.fileId = fileId;
        this.chunkNo = chunkNo;

        message = new GetChunkMessage(fileId, chunkNo);
        listener = new ChunkListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                fileId,
                chunkNo);
    }
}
