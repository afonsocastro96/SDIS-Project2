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
    private final AtomicBoolean opened;

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
    private final ConcurrentArrayList<SSLManager> connections;

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
    }

    /**
     * Start the server
     * @return true if successful, false otherwise
     */
    public boolean start() {
        // Create the SSL socket
        final SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            serverSocket = (SSLServerSocket) factory.createServerSocket(port, MAX_CONNECTIONS, InetAddress.getByName(host));
            Node.getLogger().log(Level.INFO, "Created a secure server at " + host + ":" + port);
        } catch (IOException e) {
            serverSocket = null;
            Node.getLogger().log(Level.FATAL, "Could not create a secure socket at " + host + ":" + port + ". " + e.getMessage());
            return false;
        }

        opened.set(true);
        new Thread(this).start();
        return true;
    }

    /**
     * Close the server
     */
    public void close() {
        opened.set(false);
    }

    /**
     * Shutdown the server
     */
    private void shutdown() {
        try {
            serverSocket.close();
            Node.getLogger().log(Level.INFO, "Server has been closed.");
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the server at " + host + ":" + port + ". " + e.getMessage());
        }
    }

    /**
     * Runner of the server to accept new connections.
     */
    @Override
    public void run() {
        Node.getLogger().log(Level.INFO, "Accepting up to " + MAX_CONNECTIONS + " concurrent connections.");

        while(opened.get()) {
            try {
                serverSocket.setSoTimeout(1000); // 1 second to expire the accept
                final SSLSocket connectionSocket = (SSLSocket) serverSocket.accept();
                final SSLManager monitor = new SSLManager(new SSLChannel(connectionSocket));
                new Thread(monitor).start();
                connections.add(monitor);

                Node.getLogger().log(Level.INFO, connectionSocket.getInetAddress().getHostAddress() + ":" + connectionSocket.getPort() + " has connected.");
                Node.getLogger().log(Level.DEBUG, "Waiting a new connection (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                Node.getLogger().log(Level.DEBUG, "Could not accept a connection. " + e.getMessage());
            }
        }

        shutdown();
    }
}