package feup.sdis.protocol;

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
        REMOVED
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
    private final static String CRLF = new String(new byte[]{CR, LF}, StandardCharsets.US_ASCII);

    /**
     * Message type
     */
    private final Type messageType;

    /**
     * Version of the protocol message
     */
    private final double version;

    /**
     * Id of the sender
     */
    private final UUID senderId;

    /**
     * Id of the file
     */
    private final UUID fileId;

    /**
     * Number of the chunk
     */
    private final int chunkNo;

    /**
     * Replication degree
     */
    private final int replicationDeg;

    /**
     * Body of the message
     */
    private final byte[] body;

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param senderId id of the sender
     * @param fileId id of the file
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID senderId, final UUID fileId) {
        this(messageType, version, senderId, fileId, -1);
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID senderId, final UUID fileId, final int chunkNo) {
        this(messageType, version, senderId, fileId, chunkNo, new byte[] {});
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param body body of the message
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID senderId, final UUID fileId, final int chunkNo, final byte[] body) {
        this(messageType, version, senderId, fileId, chunkNo, -1, body);
    }

    /**
     * Constructor of ProtocolMessage
     * @param messageType type of the message
     * @param version version of the message
     * @param senderId id of the sender
     * @param fileId id of the file
     * @param chunkNo number of the chunk
     * @param replicationDeg replication degree
     * @param body body of the message
     */
    public ProtocolMessage(final Type messageType, final double version, final UUID senderId, final UUID fileId, final int chunkNo, final int replicationDeg, final byte[] body) {
        this.messageType = messageType;
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
        this.body = body;
    }

    /**
     * Get the header of the message
     * @return header of the message
     */
    public String getHeader() {
        StringJoiner sj = new StringJoiner(" ", "", CRLF);
        sj.add(messageType.toString())
                .add("" + version)
                .add("" + senderId)
                .add("" + (fileId != null ? fileId : ""))
                .add("" + (chunkNo >= 0 ? chunkNo : ""))
                .add("" + (replicationDeg >= 0 ? replicationDeg : ""));

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
     * Get the sender id of the message
     * @return sender id of the message
     */
    public UUID getSenderId() {
        return senderId;
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
     * Get the replication degree of the message
     * @return replication degree of the message
     */
    public int getReplicationDeg() {
        return replicationDeg;
    }

    /**
     * Get the bytes of the message
     * @return bytes of the message
     */
    public byte[] getBytes() {
        byte [] headerBytes = getHeader().concat(CRLF).getBytes(StandardCharsets.US_ASCII);

        byte [] bytes = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, bytes, 0, headerBytes.length);
        System.arraycopy(body, 0, bytes, headerBytes.length, body.length);

        return bytes;
    }
}
