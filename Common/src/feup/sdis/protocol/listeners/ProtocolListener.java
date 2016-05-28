package feup.sdis.protocol.listeners;

import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.Observer;

/**
 * Protocol Listener
 */
public abstract class ProtocolListener implements Observer {

    /**
     * Protocol message received by the listener
     */
    protected ProtocolMessage protocolMessage;

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

    /**
     * Returns the message retrieved from the listener.
     * @return The message.
     */
    public ProtocolMessage getMessage(){
        return protocolMessage;
    }
}