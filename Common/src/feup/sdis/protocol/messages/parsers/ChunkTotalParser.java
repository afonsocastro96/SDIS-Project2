package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkTotalMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.UUID;

/**
 * Chunk total parser
 */
public class ChunkTotalParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if(header.size() != 5)
            throw new MalformedMessageException("Wrong number of arguments for the CHUNKTOTAL message: 5 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.CHUNKTOTAL.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate file id */
        if(!validFileId(header.get(2)))
            throw new MalformedMessageException("File ID must be an UUID");

        /* Validate chunk number */
        if(!validChunkNo(header.get(3)))
            throw new MalformedMessageException("Chunk Number must be an integer smaller than 1000000");

        /* Validate file name */
        if(!validFileName(header.get(4)))
            throw new MalformedMessageException("File name must have between 1 and 256 characters");

        return new ChunkTotalMessage(UUID.fromString(header.get(2)), Integer.parseInt(header.get(3)), header.get(4));
    }
}
