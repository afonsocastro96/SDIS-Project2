package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/**
 * SSL Channel
 */
public class SSLChannel {

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
     * Constructor of SSLChannel
     *
     * @param host host to connect
     * @param port port to connect
     */
    public SSLChannel(final String host, final int port) {
        this.host = host;
        this.port = port;

        // Create the SSL socket
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            socket = (SSLSocket) factory.createSocket(host, port);
            Node.getLogger().log(Level.INFO, "Established a secure connection with the relay server at " + host + ":" + port);
        } catch (IOException e) {
            socket = null;
            Node.getLogger().log(Level.FATAL, "Could not establish a secure socket to " + host + ":" + port + ". " + e.getMessage());
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
