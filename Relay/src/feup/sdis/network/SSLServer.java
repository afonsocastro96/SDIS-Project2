package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.listeners.FileNameListener;
import feup.sdis.listeners.WhoAmIListener;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.*;

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
    private final Map<SSLManager, UUID> connections;

    /**
     * Constructor of SSLServer
     *
     * @param host host of the server
     * @param port port of the server
     */
    public SSLServer(final String host, final int port) {
        this.opened = new AtomicBoolean(false);
        this.listeners = Arrays.asList(new DeleteListener(), new GetChunkListener(), new PutChunkListener(), new RemovedListener(), new StoredListener(), new FileNameListener(), new HasChunkListener(), new WhoAmIListener());
        this.host = host;
        this.port = port;
        this.connections = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Get a connection given a host and port
     * @param host host to get the connection
     * @param port port to get the connection
     * @return connection with the given host and port
     */
    public SSLManager getConnection(final String host, final int port) {
        for(Map.Entry<SSLManager, UUID> entry : connections.entrySet())
            if(entry.getKey().getChannel().getHost().equalsIgnoreCase(host) && entry.getKey().getChannel().getPort() == port)
                return entry.getKey();
        return null;
    }

    /**
     * Get the UUID of a given address
     * @param host host address to get the UUID
     * @param port port to get the UUID
     * @return UUID with that host and address
     */
    public UUID getUUID(final String host, final int port) {
        for(Map.Entry<SSLManager, UUID> entry : connections.entrySet())
            if(entry.getKey().getChannel().getHost().equalsIgnoreCase(host) && entry.getKey().getChannel().getPort() == port)
                return entry.getValue();
        return null;
    }

    /**
     * Set the UUID of a host and port
     * @param host host to be set
     * @param port port to be set
     * @param uuid uuid of the host and port
     */
    public void setUUID(final String host, final int port, final UUID uuid) {
        final SSLManager monitor = getConnection(host, port);
        if(monitor == null)
            return;

        connections.put(monitor, uuid);
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

                connections.put(monitor, null);

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

        final SSLManager monitor = (SSLManager) o;

        final Object[] objects = (Object[]) arg;
        if(!(objects[0] instanceof String))
            return;
        if(!(objects[1] instanceof Integer))
            return;

        if(objects[2] instanceof EOFException) {
            connections.remove(monitor);
            Node.getLogger().log(Level.INFO, monitor.getChannel().getHost() + ":" + monitor.getChannel().getPort() + " has disconnected (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
        } else if(objects[2] instanceof IOException) {
            connections.remove(monitor);
            Node.getLogger().log(Level.INFO, "Could not read data from host. " + ((IOException) objects[2]).getMessage() + " (" + connections.size() + "/" + MAX_CONNECTIONS + ")");
        }
    }
}