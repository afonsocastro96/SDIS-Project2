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
     * Constructor of ProtocolInitiator
     *
     * @param monitor monitor of this initiator
     */
    public HasChunkInitiator(final SSLManager monitor, final UUID fileId) {
        super(monitor);
        this.fileId = fileId;

        message = new HasChunkMessage(this.fileId);
        listener = new OkListener(monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                message.getHeader());
    }
}
