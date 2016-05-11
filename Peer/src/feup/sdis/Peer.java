package feup.sdis;

import feup.sdis.logger.Level;

/**
 * Peer of the Distributed Backup Service Over The Internet
 */
public class Peer extends Node {

    /**
     * Instance of the peer
     */
    private static Peer instance;

    /**
     * Main method of the program
     *
     * @param args arguments sent to the console
     */
    public static void main(String[] args) {
        // Parse the arguments
        Level minLevel = Level.WARNING;
        for (String arg : args) {
            switch (arg) {
                case "log=DEBUG":
                    minLevel = Level.DEBUG;
                    break;
                case "log=INFO":
                    minLevel = Level.INFO;
                    break;
                case "log=WARNING":
                    minLevel = Level.WARNING;
                    break;
                case "log=ERROR":
                    minLevel = Level.ERROR;
                    break;
                case "log=FATAL":
                    minLevel = Level.FATAL;
                    break;
            }
        }
        instance = new Peer(minLevel);

        // Starting the peer
        getLogger().log(Level.INFO, "Starting the peer");

        // Start the peer
        getLogger().log(Level.INFO, "Service started");

        // Stop the peer
        getLogger().log(Level.INFO, "Service stopped");
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
     * @param minLevel minimum level to log the messages
     */
    private Peer(final Level minLevel) {
        super("Peer", minLevel);

        // Environment variables for SSL
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }
}