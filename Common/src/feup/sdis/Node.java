package feup.sdis;

import feup.sdis.logger.Level;
import feup.sdis.logger.Logger;
import feup.sdis.utils.FileUtils;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;

/**
 * Node class that is common to every computer in the network
 */
public abstract class Node {

    /**
     * String to hold the name of the server key.
     */
    static final String KEY_STORE = "security" + File.separator + "serverKeyStore";

    /**
     * Configuration file
     */
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
     * Get the serial number
     * @return serial number
     */
    static UUID getSerialNumber() {
        Process process;
        try {
            if(System.getProperty("os.name").toLowerCase().contains("win"))
                process = Runtime.getRuntime().exec(new String[] { "wmic", "csproduct", "get", "uuid" });
            else
                process = Runtime.getRuntime().exec(new String[] { "dmidecode", "-s", "system-uuid" });
            process.getOutputStream().close();
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not retrieve the BIOS serial number. " + e.getMessage());
            return null;
        }
        final Scanner sc = new Scanner(process.getInputStream());
        if(System.getProperty("os.name").toLowerCase().contains("win")) sc.next();
        else if(!sc.hasNext())
             throw new IllegalStateException("The program must be executed with root privileges.");
        return UUID.fromString(sc.next());
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
