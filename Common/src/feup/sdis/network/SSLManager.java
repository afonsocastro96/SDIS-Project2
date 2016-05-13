package feup.sdis.network;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSL Manager
 */
public class SSLManager extends Observable implements Runnable {

    /**
     * Boolean to check if manager is running
     */
    private final AtomicBoolean running;

    /**
     * Channel to be monitored
     */
    private final SSLChannel channel;

    /**
     * Constructor of SSLManager
     *
     * @param channel channel to be monitored
     */
    public SSLManager(final SSLChannel channel) {
        this.running = new AtomicBoolean();
        this.channel = channel;
    }

    /**
     * Get the channel of the manager
     * @return channel of the manager
     */
    public SSLChannel getChannel() {
        return channel;
    }

    /**
     * Runner of the monitor to accept incoming messages
     */
    @Override
    public void run() {
        running.set(true);

        // Connect to the channel
        if (!channel.connect()) return;

        // Read messages from the channel
        byte[] data;
        while (running.get()) {
            try {
                data = channel.read();
                setChanged();
                notifyObservers(data);
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                setChanged();
                notifyObservers(e);
                running.set(false);
            }
        }

        // Disconnect from the channel
        channel.disconnect();
    }

    /**
     * Write a object to the socket
     *
     * @param data data to be written
     */
    public void write(byte[] data) throws IOException {
        if (!running.get())
            throw new IOException();
        channel.write(data);
    }
}
