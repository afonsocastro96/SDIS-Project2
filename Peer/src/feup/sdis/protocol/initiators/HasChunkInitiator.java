package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.HasChunkMessage;

import java.util.UUID;

/**
 * Created by Afonso on 29/05/2016.
 */
public class HasChunkInitiator extends ProtocolInitiator {

    UUID fileId;

    /**
     * Constructor of ProtocolInitiator
     *
     * @param monitor monitor of this initiator
     */
    public HasChunkInitiator(SSLManager monitor, UUID fileId) {
        super(monitor);
        this.fileId = fileId;

        message = new HasChunkMessage(fileId);
        listener = new OkListener(monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                message.getHeader());

        
    }
}
