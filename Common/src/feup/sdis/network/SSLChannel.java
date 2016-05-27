package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSL Channel
 */
public class SSLChannel {

    /**
     * Boolean to control if channel is connected
     */
    private final AtomicBoolean connected;

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
     * Data output stream of the connection
     */
    private DataOutputStream output;

    /**
     * Data input stream of the connection
     */
    private DataInputStream input;

    /**
     * Constructor of SSLChannel
     *
     * @param host host to connect
     * @param port port to connect
     */
    public SSLChannel(final String host, final int port) {
        this.connected = new AtomicBoolean(false);
        this.host = host;
        this.port = port;
    }

    /**
     * Constructor of SSLChannel
     *
     * @param socket socket of the channel
     */
    public SSLChannel(final SSLSocket socket) {
        this.connected = new AtomicBoolean(false);
        this.socket = socket;
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
    }

    /**
     * Get the host of the channel
     * @return host of the channel
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port of the channel
     * @return port of the channel
     */
    public int getPort() {
        return port;
    }

    /**
     * Open the connection to the server
     *
     * @return true if successful, false otherwise
     */
    public boolean connect() {
        if (connected.get()) {
            Node.getLogger().log(Level.WARNING, "A connection is already established to " + host + ":" + port + ".");
            return false;
        }

        // Create the SSL socket
        if (socket == null) {
            final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try {
                socket = (SSLSocket) factory.createSocket(host, port);
                Node.getLogger().log(Level.INFO, "Established a secure connection to " + host + ":" + port);
            } catch (IOException e) {
                socket = null;
                Node.getLogger().log(Level.FATAL, "Could not establish a secure socket to " + host + ":" + port + ". " + e.getMessage());
                return false;
            }
        }

        // Configure data streams
        try {
            socket.setSoTimeout(1000); // Wait up to one second
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            Node.getLogger().log(Level.FATAL, "Could not get the data streams of " + host + ":" + port + ". " + e.getMessage());
            disconnect();
            return false;
        }

        connected.set(true);
        return true;
    }

    /**
     * Close the connection
     */
    public void disconnect() {
        connected.set(false);

        try {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            if (socket != null)
                socket.close();
            socket = null;
            Node.getLogger().log(Level.INFO, "Connection to " + host + ":" + port + " was closed.");
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the channel connection to " + host + ":" + port + ". " + e.getMessage());
        }
    }

    /**
     * Read a message from the channel
     * @return message that was read
     * @throws IOException when an error occurs on read
     */
    public byte[] read() throws IOException {
        int size = readMessageSize();
        byte[] data = readMessageContent(size);
        Node.getLogger().log(Level.DEBUG, "Received a message with size of " + size + " bytes.");
        return data;
    }

    /**
     * Read the message size from the channel
     * @return size of the received message
     * @throws IOException when an error occurs on read
     */
    private int readMessageSize() throws IOException {
        return input.readInt();
    }

    /**
     * Read the content of a message
     * @param size size of the message to read
     * @return content of the message
     * @throws IOException when an error occurs on read
     */
    private byte[] readMessageContent(final int size) throws IOException {
        byte[] buffer = new byte[size];
        if(input.read(buffer) == -1)
            throw new EOFException();
        return buffer;
    }

    /**
     * Write a object to the socket
     *
     * @param data data to be written
     */
    public void write(final byte[] data) throws IOException {
        output.writeInt(data.length);
        output.write(data);
        Node.getLogger().log(Level.DEBUG, "Sent a message with size of " + data.length + " bytes.");
    }
}
