package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.*;
import feup.sdis.utils.ConcurrentArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSL Server
 */
public class SSLServer implements Runnable, Observer {

    /**
     * Maximum connections to the relay server
     */
    private static final int MAX_CONNECTIONS = 100;

    /**
     * Boolean to control if the server is opened
     */
    private final AtomicBoolean opened;

    /**
     * Listeners of the server
     */
    private final List<ProtocolListener> listeners;

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
        this.listeners = Arrays.asList(new DeleteListener(), new GetChunkListener(), new PutChunkListener(), new RemovedListener(), new StoredListener());
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
            serverSocket.setSoTimeout(1000); // 1 second
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
                final SSLSocket connectionSocket = (SSLSocket) serverSocket.accept();
                final SSLManager monitor = new SSLManager(new SSLChannel(connectionSocket));
                monitor.addObserver(this);
                listeners.forEach(monitor::addObserver);
                monitor.start();

                connections.add(monitor);

                Node.getLogger().log(Level.INFO, connectionSocket.getInetAddress().getHostAddress() + ":" + connectionSocket.getPort() + " has connected (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                Node.getLogger().log(Level.DEBUG, "Could not accept a connection. " + e.getMessage());
            }
        }

        shutdown();
    }

    /**
     * Update method to receive updates when a SSLManager changes its status
     * @param o observable that called the function
     * @param arg arguments of the function
     */
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;

        if(arg == null)
            return;

        final SSLManager monitor = (SSLManager) o;

        if(arg instanceof EOFException) {
            connections.remove((SSLManager) o);
            Node.getLogger().log(Level.INFO, monitor.getChannel().getHost() + ":" + monitor.getChannel().getPort() + " has disconnected (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
        } else if (arg instanceof IOException) {
            connections.remove((SSLManager) o);
            Node.getLogger().log(Level.INFO, "Could not read data from host. " + ((IOException) arg).getMessage() + " (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
        }
    }
}