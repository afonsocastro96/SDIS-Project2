package feup.sdis.protocol.initiators;

import feup.sdis.Peer;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.WhoAmIMessage;

/**
 * Who am I initiator
 */
public class WhoAmIInitiator extends ProtocolInitiator {

    /**
     * Constructor of WhoAmIInitiator
     * @param monitor monitor of this channel
     */
    public WhoAmIInitiator(final SSLManager monitor) {
        super(monitor);

        message = new WhoAmIMessage(Peer.getInstance().getId());
        listener = new OkListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                message.getHeader());
    }
}
