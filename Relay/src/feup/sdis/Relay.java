package feup.sdis;

import feup.sdis.utils.FileUtils;

import java.io.*;

/**
 * Peer of the Distributed Backup Service Over The Internet
 */
public class Relay {

    /**
     * Instance of the relay server
     */
    private static Relay instance;

    /**
     * String to hold the name of the server key.
     */
    private static final String KEY_STORE = "security" + File.separator + "serverKeyStore";

    /**
     * Main method of the program
     *
     * @param args arguments sent to the console
     */
    public static void main(String[] args) {
        instance = new Relay();
    }

    /**
     * Get the instance of the relay
     *
     * @return instance of the relay
     */
    public static Relay getInstance() {
        return instance;
    }

    /**
     * Constructor of Relay
     */
    private Relay() {
        // Configure SSL
        createKey();
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE);
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    }

    /**
     * Create the key file
     */
    private void createKey() {
        File keyFile = new File(KEY_STORE);
        if(keyFile.exists())
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