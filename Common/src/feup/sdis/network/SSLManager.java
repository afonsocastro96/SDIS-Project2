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
     * Retry attempt
     */
    private int retryAttempt;

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
        this.retryAttempt = 0;
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
     * Get the retry attempt
     * @return retry attempt
     */
    public int getRetryAttempt() {
        return retryAttempt;
    }

    /**
     * Check if the SSLManager is running
     * @return true if is running
     */
    public boolean isRunning() {
        return running.get();
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
        retryAttempt++;

        Node.getLogger().log(Level.INFO, "Retrying to connect to the server in " + (retryAttempt * 2) + " seconds.");

        start();
    }

    /**
     * Runner of the monitor to accept incoming messages
     */
    @Override
    public void run() {
        try {
            if(retryAttempt > 0) {
                int connectDelay = retryAttempt * 2000;
                if(connectDelay > 60000)
                    connectDelay = 60000;
                Thread.sleep(connectDelay);
            }
        } catch (InterruptedException ignored) {
        }

        // Save the host and port of the connection to the listeners
        final Object[] objects = new Object[3];
        objects[0] = channel.getHost();
        objects[1] = channel.getPort();

        // Connect to the channel
        if (!channel.connect()) {
            setChanged();
            objects[2] = new SocketException("Could not establish connection");
            notifyObservers(objects);
            return;
        }
        retryAttempt = 0;
        running.set(true);

        // Read messages from the channel
        while (running.get()) {
            try {
                objects[2] = channel.read();
                setChanged();
                notifyObservers(objects);
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                objects[2] = e;
                setChanged();
                notifyObservers(objects);
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