package feup.sdis;

import feup.sdis.logger.Level;
import feup.sdis.logger.Logger;
import feup.sdis.utils.FileUtils;

import java.io.*;
import java.util.Scanner;

/**
 * Node class that is common to every computer in the network
 */
public abstract class Node {

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
     * Get the BIOS serial number
     * @return BIOS serial number
     */
    static String getBiosSerialNumber() {
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "serialnumber" });
            process.getOutputStream().close();
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not retrieve the BIOS serial number. " + e.getMessage());
            return null;
        }
        final Scanner sc = new Scanner(process.getInputStream());
        sc.next();
        return sc.next();
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
