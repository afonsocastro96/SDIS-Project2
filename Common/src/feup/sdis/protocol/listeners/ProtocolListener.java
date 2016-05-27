package feup.sdis.protocol.listeners;

import java.util.Observer;

/**
 * Protocol Listener
 */
public abstract class ProtocolListener implements Observer {

    /**
     * Flag to check if we have already received the expected response
     */
    protected boolean receivedResponse;

    /**
     * Check if we have received the expected response
     * @return true if we received the expected response
     */
    public boolean hasReceivedResponse() {
        return receivedResponse;
    }
}