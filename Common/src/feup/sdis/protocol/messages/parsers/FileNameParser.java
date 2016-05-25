package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.FileNameMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.UUID;

/**
 * File name parser
 */
public class FileNameParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);
        if (header.size() != 4)
            throw new MalformedMessageException("Wrong number of arguments for the FILENAME message: 4 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.FILENAME.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if(!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate file ID */
        if (!validFileId(header.get(2)))
            throw new MalformedMessageException("File ID must be an UUID");

        /* Validate file name */
        if(!validFileName(header.get(3)))
            throw new MalformedMessageException("File name must have between 1 and 256 characters");

        return new FileNameMessage(UUID.fromString(header.get(2)), header.get(3));
    }
}
