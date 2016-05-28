package feup.sdis.protocol;

/**
 * Protocol
 */
public class Protocol {

    /**
     * Version of the protocol
     */
    public static final int VERSION = 1;

    /**
     * Size of each chunk
     */
    public static final int CHUNK_SIZE = 65536;

    /**
     * Encrypt algorithm
     */
    public static final String ENCRYPT_ALGORITHM = "AES";
}
