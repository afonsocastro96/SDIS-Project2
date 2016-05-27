package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
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
}