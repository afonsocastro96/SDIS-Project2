package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkMessage;

import java.util.UUID;

/**
 * Chunk parser
 */
public class ChunkParser extends ProtocolParser {

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
            throw new MalformedMessageException("Wrong number of arguments for the CHUNK message: 4 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.CHUNK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate file ID */
        if (!validFileId(header.get(2)))
            throw new MalformedMessageException("File ID must be an UUID");

        /* Validate chunk No */
        if (!validChunkNo(header.get(3)))
            throw new MalformedMessageException("Chunk Number must be an integer smaller than 1000000");

        return new ChunkMessage(UUID.fromString(header.get(2)), Integer.parseInt(header.get(3)), body);
    }
}
