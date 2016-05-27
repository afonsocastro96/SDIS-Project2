package feup.sdis.initiators;

import feup.sdis.Peer;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.WhoAmIMessage;

/**
 * Who am I initiator
 */
public class WhoAmIInitiator extends ProtocolInitiator {

    /**
     * Constructor of WhoAmIInitiator
     */
    public WhoAmIInitiator() {
        message = new WhoAmIMessage(Peer.getInstance().getId());
        listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
    }
}
