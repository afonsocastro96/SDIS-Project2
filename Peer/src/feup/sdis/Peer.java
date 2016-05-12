package feup.sdis;

import feup.sdis.logger.Level;
import feup.sdis.network.SSLChannel;
import feup.sdis.network.SSLManager;

import java.io.*;
import java.util.Properties;

/**
 * Peer of the Distributed Backup Service Over The Internet
 */
public class Peer extends Node {

    /**
     * Instance of the peer
     */
    private static Peer instance;

    /**
     * Id of the peer
     */
    private final String id;

    /**
     * Monitor of the connection channel to the relay server
     */
    private SSLManager monitor;

    /**
     * Connection channel to the relay server
     */
    private SSLChannel channel;

    /**
     * Main method of the program
     *
     * @param args arguments sent to the console
     */
    public static void main(String[] args) {
        // ID of the peer
        final String id = getBiosSerialNumber();
        if(id == null)
            return;

        instance = new Peer(id);

        // Starting the peer
        getLogger().log(Level.INFO, "Starting the service of " + getInstance().getId());

        if(!getInstance().createConfig())
            return;
        if(!getInstance().loadConfig())
            return;
        new Thread(getInstance().getMonitor()).start();
        try {
            Thread.sleep(1000);
            getInstance().getMonitor().write("Cenas".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start the server
        getLogger().log(Level.INFO, "Service started.");
    }

    /**
     * Get the instance of the peer
     *
     * @return instance of the peer
     */
    public static Peer getInstance() {
        return instance;
    }

    /**
     * Constructor of Peer
     *
     * @param id id of the peer
     */
    private Peer(final String id) {
        super("Peer");

        this.id = id;

        // Environment variables for SSL
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }

    /**
     * Get the ID of the peer
     * @return id of the peer
     */
    public String getId() {
        return id;
    }

    /**
     * Get the secure channel monitor of the peer
     * @return secure channel monitor of the peer
     */
    public SSLManager getMonitor() {
        return monitor;
    }

    /**
     * Get the channel of the peer
     * @return channel of the peer
     */
    public SSLChannel getChannel() {
        return channel;
    }

    /**
     * Create the configuration file
     *
     * @return true if successful, false otherwise
     */
    @Override
    boolean createConfig() {
        File configFile = new File(CONFIG_FILE);
        if(configFile.exists()) return true;

        Properties properties = new Properties();
        OutputStream output = null;

        try {
            if(!configFile.createNewFile()) {
                getLogger().log(Level.FATAL, "Could not create the configuration file.");
                return false;
            }

            output = new FileOutputStream(configFile);

            // Set the properties values
            properties.setProperty("log", "info");
            properties.setProperty("relayhost", "127.0.0.1");
            properties.setProperty("relayport", "21852");

            // Save the file
            properties.store(output, null);

            getLogger().log(Level.INFO, "Configuration file has been created.");
            return true;
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not create the configuration file. " + e.getMessage());
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    getLogger().log(Level.FATAL, "Could not close the configuration file. " + e.getMessage());
                }
            }
        }
    }

    /**
     * Load the configuration file
     *
     * @return true if successful, false otherwise
     */
    @Override
    boolean loadConfig() {
        Properties properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE);

            // Load the properties file
            properties.load(input);

            // Logger
            String logLevel = properties.getProperty("log");
            try {
                getLogger().setLevel(Level.valueOf(logLevel.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                getLogger().log(Level.WARNING, "Invalid value for log property. Using default " + getLogger().getLevel());
            }
            getLogger().log(Level.DEBUG, "Log level - " + logLevel);

            // Relay server
            String host = properties.getProperty("relayhost");
            getLogger().log(Level.DEBUG, "Relay server host - " + host);

            int port;
            try {
                port = Integer.parseInt(properties.getProperty("relayport"));
                getLogger().log(Level.DEBUG, "Relay server port - " + port);
            } catch (NumberFormatException ignored) {
                getLogger().log(Level.FATAL, "Invalid value for relay server port property.");
                return false;
            }
            channel = new SSLChannel(host, port);
            monitor = new SSLManager(channel);

            getLogger().log(Level.INFO, "Configuration has been loaded.");
            return true;
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not load the configuration file. " + e.getMessage());
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    getLogger().log(Level.FATAL, "Could not close the configuration file. " + e.getMessage());
                }
            }
        }
    }
}