package feup.sdis.protocol.messages.parsers;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.StoredMessage;

import java.util.UUID;

/**
 * Stored parser
 */
public class StoredParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if (header.size() != 5)
            throw new MalformedMessageException("Wrong number of arguments for the STORED message: 5 arguments must be present");

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.STORED.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        if (!validSenderId(header.get(2)))
            throw new MalformedMessageException("Sender ID must be an UUID");

        /* Validate file ID */
        if (!validFileId(header.get(3)))
            throw new MalformedMessageException("File ID must be an UUID");

        /* Validate chunk No */
        if (!validChunkNo(header.get(4)))
            throw new MalformedMessageException("Chunk Number must be an integer smaller than 1000000");

        return new StoredMessage(UUID.fromString(header.get(2)), UUID.fromString(header.get(3)), Integer.parseInt(header.get(4)));
    }
}
