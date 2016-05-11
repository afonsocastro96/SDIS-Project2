package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.utils.ConcurrentArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSL Server
 */
public class SSLServer implements Runnable {

    /**
     * Maximum connections to the relay server
     */
    private static final int MAX_CONNECTIONS = 100;

    /**
     * Boolean to control if the server is opened
     */
    private AtomicBoolean opened;

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
    private final ConcurrentArrayList<SSLSocket> connections;

    /**
     * Constructor of SSLServer
     *
     * @param host host of the server
     * @param port port of the server
     */
    public SSLServer(final String host, final int port) {
        this.opened = new AtomicBoolean(false);
        this.host = host;
        this.port = port;
        this.connections = new ConcurrentArrayList<>();

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
     * Open the server
     */
    public void open() {
        opened.set(true);
        new Thread(this).start();
    }

    /**
     * Close the server
     */
    public void close() {
        opened.set(false);
    }

    /**
     * Runner of the server to accept new connections.
     */
    @Override
    public void run() {
        Node.getLogger().log(Level.INFO, "Accepting up to " + MAX_CONNECTIONS + " concurrent connections.");

        while(opened.get()) {
            Node.getLogger().log(Level.DEBUG, "Waiting a new connection (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
            try {
                serverSocket.setSoTimeout(1000); // 1 second to expire the accept
                SSLSocket connectionSocket = (SSLSocket) serverSocket.accept();
                connections.add(connectionSocket);
                Node.getLogger().log(Level.INFO, "Accepted incoming connection from " + connectionSocket.getInetAddress().getHostAddress() + ":" + connectionSocket.getPort());
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                Node.getLogger().log(Level.DEBUG, "Could not accept a connection. " + e.getMessage());
            }
        }
    }
}