package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.DeleteMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

/**
 * Delete parser
 */
public class DeleteParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if (header.size() != 3)
            throw new MalformedMessageException("Wrong number of arguments for the DELETE message: 3 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.DELETE.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate file ID */
        if (!validFileId(header.get(2)) && !validFileName(header.get(2)))
            throw new MalformedMessageException("File ID must be an UUID or a file path");

        return new DeleteMessage(header.get(2));
    }
}
