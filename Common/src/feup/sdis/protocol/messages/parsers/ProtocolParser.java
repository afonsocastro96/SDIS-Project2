package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.listeners.ProtocolListener;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.exceptions.MalformedMessageException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Protocol Parser
 */
public abstract class ProtocolParser {

    /**
     * Header of the message
     */
    protected List<String> header;

    /**
     * Body of the message
     */
    protected byte[] body;

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    public abstract ProtocolMessage parse(byte[] message) throws MalformedMessageException;

    /**
     * Split the message
     * @param message message to split
     */
    protected void splitMessage(byte[] message) {
        int i = 0;

        // Get the header
        for (int j = 0; j < message.length - 1; ++j) {
            if ((message[j] == ProtocolMessage.CR) && (message[j+1] == ProtocolMessage.LF)){
                if (j == i) {
                    i = j + 2;
                    break;
                }
                header = Arrays.asList(new String(Arrays.copyOfRange(message, i, j), StandardCharsets.US_ASCII).trim().replaceAll("\\s+", " ").split(" "));
                i = j + 2;
            }
        }

        // Get the body
        body = Arrays.copyOfRange(message, i, message.length);
    }

    /**
     * Check if the version is valid
     * @param version version to check
     * @return true if valid, false otherwise
     */
    protected boolean validVersion(String version) {
        return version.matches("^\\d.\\d$") && Double.parseDouble(version) <= Protocol.VERSION;
    }

    /**
     * Check if the sender id is valid
     * @param senderId sender id to check
     * @return true if valid, false otherwise
     */
    protected boolean validSenderId(String senderId) {
        return senderId.split("-").length == 5;
    }

    /**
     * Check if the file id is valid
     * @param fileId file id to check
     * @return true if valid, false otherwise
     */
    protected boolean validFileId(String fileId) {
        return fileId.split("-").length == 5;
    }

    /**
     * Check if the chunk number is valid
     * @param chunkNo chunk number to check
     * @return true if valid, false otherwise
     */
    protected boolean validChunkNo(String chunkNo) {
        return chunkNo.matches("^\\d{0,6}$");
    }

    /**
     * Check if the replication degree is valid
     * @param replicationDeg replication degree to check
     * @return true if valid, false otherwise
     */
    protected boolean validReplicationDeg(String replicationDeg) {
        return replicationDeg.matches("^\\d$");
    }
}
