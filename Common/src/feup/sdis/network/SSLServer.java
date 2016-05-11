package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * SSL Server
 */
public class SSLServer implements Runnable {

    /**
     * Maximum connections to the relay server
     */
    private static final int MAX_CONNECTIONS = 100;

    /**
     * Host of the server
     */
    private final String host;

    /**
     * Port of the server
     */
    private final int port;

    /**
     * Server socket
     */
    private SSLServerSocket serverSocket;

    /**
     * List with connected peers
     */
    private final List<SSLSocket> connections;

    /**
     * Constructor of SSLServer
     *
     * @param host host of the server
     * @param port port of the server
     */
    public SSLServer(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.connections = new ArrayList<>();

        // Create the SSL socket
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            serverSocket = (SSLServerSocket) factory.createServerSocket(port, MAX_CONNECTIONS, InetAddress.getByName(host));
            Node.getLogger().log(Level.INFO, "Created a secure server at " + host + ":" + port);
        } catch (IOException e) {
            serverSocket = null;
            Node.getLogger().log(Level.FATAL, "Could not create a secure socket at " + host + ":" + port + ". " + e.getMessage());
        }
    }

    /**
     * Get the server socket
     * @return server socket
     */
    public SSLServerSocket getSocket() {
        return serverSocket;
    }

    /**
     * Runner of the server
     */
    @Override
    public void run() {
        try {
            SSLSocket connectionSocket = (SSLSocket) serverSocket.accept();
            connections.add(connectionSocket);
            Node.getLogger().log(Level.INFO, "Accepted incoming connection from " + connectionSocket.getInetAddress().getHostAddress() + ":" + connectionSocket.getPort());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not accept a foreign connection. " + e.getMessage());
        }
    }
}
