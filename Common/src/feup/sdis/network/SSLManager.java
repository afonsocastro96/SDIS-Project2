package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import java.io.IOException;
import java.net.SocketException;
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
     * Delay in millis to restart monitoring a channel
     */
    private int retryDelay;

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
        this.retryDelay = 0;
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
     * Start monitoring the channel
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Stop monitoring the channel
     */
    public void stop() {
        running.set(false);
    }

    /**
     * Retry to monitor the channel
     */
    public void retry() {
        if(retryDelay == 0)
            retryDelay = 1000;
        else if (retryDelay < 60000)
            retryDelay *= 2;
        else
            retryDelay = 60000;

        Node.getLogger().log(Level.INFO, "Retrying to connect to the server in " + (retryDelay / 1000) + " seconds.");

        start();
    }

    /**
     * Runner of the monitor to accept incoming messages
     */
    @Override
    public void run() {
        try {
            Thread.sleep(retryDelay);
        } catch (InterruptedException ignored) {
        }

        // Connect to the channel
        if (!channel.connect()) {
            setChanged();
            notifyObservers(new SocketException());
            return;
        }
        retryDelay = 0;
        running.set(true);

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
            throw new IOException("Service not running");
        channel.write(data);
    }
}