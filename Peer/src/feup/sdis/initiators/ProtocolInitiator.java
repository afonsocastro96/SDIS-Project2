package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.ProtocolListener;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.io.IOException;

/**
 * Protocol Listener
 */
public abstract class ProtocolInitiator implements Runnable {

    /**
     * Maximum attempts to send the message
     */
    protected final int MAX_ATTEMPTS = 3;

    /**
     * Maximum rounds before attempting to resend the message
     */
    protected final int MAX_ROUNDS = 5;

    /**
     * Message of the protocol
     */
    protected ProtocolMessage message;

    /**
     * Listener for the reception of the message
     */
    protected ProtocolListener listener;

    /**
     * Number of attempts to send the message
     */
    private int attempts;

    /**
     * Number of waiting rounds
     */
    private int rounds;

    /**
     * Constructor of ProtocolInitiator
     */
    public ProtocolInitiator() {
        this.attempts = 0;
        this.rounds = 0;
    }

    /**
     * Get the number of attempts
     * @return number of attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Get the number of waiting rounds
     * @return number of waiting rounds
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Reset the number of attempts
     */
    public void resetAttempts() {
        attempts = 0;
    }

    /**
     * Reset the number of rounds
     */
    public void resetRounds() {
        rounds = 0;
    }

    /**
     * Increase the number of attempts
     */
    public void increaseAttempts() {
        ++attempts;
    }

    /**
     * Increase the number of rounds
     */
    public void increaseRounds() {
        ++rounds;
    }

    /**
     * Send a message to the channel
     * @param message message to be sent
     * @return true if successful, false otherwise
     */
    protected boolean sendMessage(final ProtocolMessage message) {
        try {
            Peer.getInstance().getMonitor().write(message.getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Check if the initiator has received the expected response
     * @return true if received, false otherwise
     */
    public boolean hasReceivedResponse() {
        return listener.hasReceivedResponse();
    }

    /**
     * Runnable of ProtocolInitiator
     */
    @Override
    public void run() {
        Peer.getInstance().getMonitor().addObserver(listener);

        while(!hasReceivedResponse()){
            if(getAttempts() >= MAX_ATTEMPTS)
                break;

            if(getRounds() == 0){
                sendMessage(message);
                increaseAttempts();
            }

            increaseRounds();
            if(getRounds() >= MAX_ROUNDS)
                resetRounds();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        Peer.getInstance().getMonitor().deleteObserver(listener);

        if(hasReceivedResponse())
            Node.getLogger().log(Level.DEBUG, "Server received the message " + message.getHeader());
        else
            Node.getLogger().log(Level.ERROR, "Could not send the message " + message.getHeader() + " to the server.");
    }

    public ProtocolMessage getResponse(){
        if(!listener.hasReceivedResponse())
            return null;
        return listener.getMessage();
    }
}