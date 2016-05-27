package feup.sdis.protocol.messages;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Protocol message
 */
public abstract class ProtocolMessage {

    /**
     * Protocol message type
     */
    public enum Type {
        /**
         * Put chunk message type
         */
        PUTCHUNK,

        /**
         * File name message type
         */
        FILENAME,

        /**
         * Stored message type
         */
        STORED,

        /**
         * Get chunk message type
         */
        GETCHUNK,

        /**
         * Chunk message type
         */
        CHUNK,

        /**
         * Delete message type
         */
        DELETE,

        /**
         * Removed message type
         */
        REMOVED,

        /**
         * OK message type
         */
        OK,

        /**
         * WHOAMI message type
         */
        WHOAMI,

        /**
         * HASCHUNK message type
         */
        HASCHUNK
    }

    /**
     * CR byte
     */
    public final static byte CR = 0x0D;

    /**
     * LF byte
     */
    public final static byte LF = 0x0A;

    /**
     * CRLF string
     */
    public final static String CRLF = new String(new byte[]{CR, LF}, StandardCharsets.US_ASCII);

    /**
     * Message type
     */
    private final Type messageType;

    /**
     * Version of the protocol message
     */
    private final double version;

    /**
     * Id of the file
     */
    private final UUID fileId;

    /**
     * Number of the chunk
     */
    private final int chunkNo;

    /**
     * Minimum replicas
     */
    private final int minReplicas;

    /**
     * Body of the message
     */
    private final byte[] body;

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     */
    public ProtocolMessage(final Type messageType, final double version){
        this(messageType, version, null);
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param fileId id of the file
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID fileId) {
        this(messageType, version, fileId, -1);
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID fileId, final int chunkNo) {
        this(messageType, version, fileId, chunkNo, new byte[] {});
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param body body of the message
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID fileId, final int chunkNo, final byte[] body) {
        this(messageType, version, fileId, chunkNo, -1, body);
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param minReplicas minimum replicas
     * @param body body of the message
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID fileId, final int chunkNo, final int minReplicas, final byte[] body) {
        this.messageType = messageType;
        this.version = version;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.minReplicas = minReplicas;
        this.body = body;
    }

    /**c
     * Get the header of the message
     * @return header of the message
     */
    public String getHeader() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(messageType.toString())
                .add("" + version)
                .add("" + (fileId != null ? fileId : ""))
                .add("" + (chunkNo >= 0 ? chunkNo : ""))
                .add("" + (minReplicas >= 0 ? minReplicas : ""));

        return sj.toString();
    }

    /**
     * Get the body of the message
     * @return body of the message
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Get the message type
     * @return type of the message
     */
    public Type getMessageType() {
        return messageType;
    }

    /**
     * Get the version of the message
     * @return version of the message
     */
    public double getVersion() {
        return version;
    }

    /**
     * Get the file id of the message
     * @return file id of the message
     */
    public UUID getFileId() {
        return fileId;
    }

    /**
     * Get the chunk number of the message
     * @return chunk number of the message
     */
    public int getChunkNo() {
        return chunkNo;
    }

    /**
     * Get the minimum replicas of the message
     * @return minimum replicas of the message
     */
    public int getMinReplicas() {
        return minReplicas;
    }

    /**
     * Get the bytes of the message
     * @return bytes of the message
     */
    public byte[] getBytes() {
        byte [] headerBytes = getHeader().concat(CRLF + CRLF).getBytes(StandardCharsets.US_ASCII);

        byte [] bytes = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, bytes, 0, headerBytes.length);
        System.arraycopy(body, 0, bytes, headerBytes.length, body.length);

        return bytes;
    }
}
