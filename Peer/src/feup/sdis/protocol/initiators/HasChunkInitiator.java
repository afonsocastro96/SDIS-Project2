package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.HasChunkMessage;

import java.util.UUID;

/**
 * Has chunk initiator
 */
public class HasChunkInitiator extends ProtocolInitiator {

    /**
     * File id to check if we are supposed to have it
     */
    private final UUID fileId;

    /**
     * Number of the chunk
     */
    private final int chunkNo;

    /**
     * Constructor of ProtocolInitiator
     *
     * @param monitor monitor of this initiator
     */
    public HasChunkInitiator(final SSLManager monitor, final UUID fileId, final int chunkNo) {
        super(monitor);
        this.fileId = fileId;
        this.chunkNo = chunkNo;

        message = new HasChunkMessage(this.fileId, this.chunkNo);
        listener = new OkListener(monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                message.getHeader());
    }
}
