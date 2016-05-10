package feup.sdis;

import feup.sdis.utils.FileUtils;

import java.io.*;

/**
 * Peer of the Distributed Backup Service Over The Internet
 */
public class Peer {

    /**
     * Instance of the peer
     */
    private static Peer instance;

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
        instance = new Peer();
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
     */
    private Peer() {
        // Configure SSL
        createKey();
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }

    /**
     * Create the key file
     */
    private void createKey() {
        final File keyFile = new File(KEY_STORE);
        if(keyFile.exists())
            return;

        if (!keyFile.getParentFile().exists())
            keyFile.getParentFile().mkdirs();

        // Saving the Public key in a file
        try {
            FileUtils.copyStream(getClass().getResourceAsStream("/resources/serverKeyStore"), new FileOutputStream(keyFile));
        } catch (final FileNotFoundException e) {
            System.out.println("Error while copying the server key store! ");
            e.printStackTrace();
        }
    }
}