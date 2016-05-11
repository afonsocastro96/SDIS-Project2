package feup.sdis;

import feup.sdis.logger.Logger;
import feup.sdis.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Node class that is common to every computer in the network
 */
abstract class Node {

    /**
     * String to hold the name of the server key.
     */
    static final String KEY_STORE = "security" + File.separator + "serverKeyStore";

    static final String CONFIG_FILE = "config.properties";

    /**
     * Logger of the relay server
     */
    private static Logger logger;

    /**
     * Constructor of Node
     *
     * @param name name of the node
     */
    Node(final String name) {
        // Logger Configuration
        try {
            logger = new Logger(name);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println("Error while creating the logger! " + e.getMessage());
        }

        // SSL Configuration
        copyKeyStore();
    }

    /**
     * Get the logger
     *
     * @return logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Create the server key store file
     */
    private void copyKeyStore() {
        File keyFile = new File(KEY_STORE);
        if (keyFile.exists())
            return;

        if (!keyFile.getParentFile().exists())
            if (!keyFile.getParentFile().mkdirs())
                return;

        // Saving the Public key in a file
        try {
            FileUtils.copyStream(getClass().getResourceAsStream("/resources/serverKeyStore"), new FileOutputStream(keyFile));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    /**
     * Create the configuration file
     * @return true if successful, false otherwise
     */
    abstract boolean createConfig();

    /**
     * Load the configuration file
     * @return true if successful, false otherwise
     */
    abstract boolean loadConfig();
}
