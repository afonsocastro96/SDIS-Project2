package feup.sdis;

import feup.sdis.logger.Logger;
import feup.sdis.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Peer of the Distributed Backup Service Over The Internet
 */
public class Relay {

    /**
     * String to hold the name of the server key.
     */
    private static final String KEY_STORE = "security" + File.separator + "serverKeyStore";

    /**
     * Instance of the relay server
     */
    private static Relay instance;

    /**
     * Logger of the relay server
     */
    private final Logger logger;

    /**
     * Main method of the program
     *
     * @param args arguments sent to the console
     */
    public static void main(String[] args) {
    }

    /**
     * Get the instance of the relay
     *
     * @return instance of the relay
     */
    public static Relay getInstance() {
        if (instance == null)
            instance = new Relay();
        return instance;
    }

    /**
     * Constructor of Relay
     */
    private Relay() {
        // Configure Logger
        logger = new Logger();

        // Configure SSL
        createKey();
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE);
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    }

    /**
     * Get the logger
     *
     * @return logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Create the key file
     */
    private void createKey() {
        File keyFile = new File(KEY_STORE);
        if (keyFile.exists())
            return;

        if (!keyFile.getParentFile().exists())
            keyFile.getParentFile().mkdirs();

        // Saving the Public key in a file
        try {
            FileUtils.copyStream(getClass().getResourceAsStream("/resources/serverKeyStore"), new FileOutputStream(keyFile));
        } catch (FileNotFoundException e) {
            System.out.println("Error while copying the server key store! ");
            e.printStackTrace();
        }
    }
}