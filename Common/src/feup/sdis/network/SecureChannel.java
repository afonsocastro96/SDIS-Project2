package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/**
 * Secure Channel
 */
public class SecureChannel {

    /**
     * Host of the channel
     */
    private final String host;

    /**
     * Port of the channel
     */
    private final int port;

    /**
     * Connection socket
     */
    private SSLSocket socket;

    /**
     * Constructor of SecureChannel
     *
     * @param host host to connect
     * @param port port to connect
     */
    public SecureChannel(final String host, final int port) {
        this.host = host;
        this.port = port;

        // Create the SSL socket
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            socket = (SSLSocket) factory.createSocket(host, port);
            Node.getLogger().log(Level.INFO, "Established a secure connection with the relay server.");
        } catch (IOException e) {
            socket = null;
            Node.getLogger().log(Level.FATAL, "Could not create a secure socket. " + e.getMessage());
        }
    }

    /**
     * Get the connection socket
     * @return connection socket
     */
    public SSLSocket getSocket() {
        return socket;
    }
}
