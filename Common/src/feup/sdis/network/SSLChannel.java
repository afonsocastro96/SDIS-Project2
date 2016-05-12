package feup.sdis.network;

import feup.sdis.Node;
import feup.sdis.logger.Level;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSL Channel
 */
public class SSLChannel extends Observable implements Runnable {

    /**
     * Maximum size per packet
     */
    private final static int MAX_SIZE_PACKET = 65000;

    /**
     * Boolean to control if the server is opened
     */
    private final AtomicBoolean opened;

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
        this.opened = new AtomicBoolean(false);
        this.host = host;
        this.port = port;
    }

    /**
     * Constructor of SSLChannel
     * @param socket socket of the channel
     */
    public SSLChannel(final SSLSocket socket) {
        this.opened = new AtomicBoolean(false);
        this.socket = socket;
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
    }

    /**
     * Open the connection to the server
     *
     * @return true if successful, false otherwise
     */
    public boolean open() {
        if(opened.get()) {
            Node.getLogger().log(Level.WARNING, "A connection is already established to " + host + ":" + port + ".");
            return false;
        }

        // Create the SSL socket
        if(socket == null) {
            final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try {
                socket = (SSLSocket) factory.createSocket(host, port);
                socket.setSoTimeout(1000); // Wait up to one second to read data
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                Node.getLogger().log(Level.INFO, "Established a secure connection with the relay server at " + host + ":" + port);
            } catch (IOException e) {
                socket = null;
                Node.getLogger().log(Level.FATAL, "Could not establish a secure socket to " + host + ":" + port + ". " + e.getMessage());
                return false;
            }
        }

        opened.set(true);
        new Thread(this).start();
        return true;
    }

    /**
     * Close the connection
     * @return true if successful, false otherwise
     */
    public boolean close() {
        opened.set(false);

        try {
            if (socket != null)
                socket.close();
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            Node.getLogger().log(Level.INFO, "Connection to the server was closed.");
            return true;
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the secure socket to " + host + ":" + port + ". " + e.getMessage());
            return false;
        }
    }

    /**
     * Runner of the channel to accept incoming messages
     */
    @Override
    public void run() {
        Object data;

        while (opened.get()) {
            data = read();
            if (data == null)
                continue;

            notifyObservers(data);
        }
    }

    /**
     * Read a object from the socket
     *
     * @return object read from the socket
     */
    private Object read() {
        final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[MAX_SIZE_PACKET];
        int bytesRead;
        try {
            // Read data
            byteArray.reset();
            do {
                bytesRead = input.read(buffer);
                if (bytesRead != -1)
                    byteArray.write(buffer, 0, bytesRead);
            } while (bytesRead != -1);
            if (byteArray.size() <= 0)
                return null;

            Node.getLogger().log(Level.DEBUG, "Received a packet with size of " + byteArray.size() + " bytes.");
            return byteArray.toByteArray();
        } catch (SocketTimeoutException | SocketException ignored) {
            return null;
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not read the data from the socket. " + e.getMessage());
            return null;
        }
    }
}
