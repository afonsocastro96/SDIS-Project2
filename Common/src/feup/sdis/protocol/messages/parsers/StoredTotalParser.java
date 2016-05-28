package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkTotalMessage;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.StoredTotalMessage;

import java.util.UUID;

/**
 * Stored total parser
 */
public class StoredTotalParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if(header.size() != 3)
            throw new MalformedMessageException("Wrong number of arguments for the STOREDTOTAL message: 3 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.STOREDTOTAL.toString()))
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

        /* Validate Replication Deg */
        if(!validReplicationDeg(header.get(4)))
            throw new MalformedMessageException("Replication Degree must be a single digit");

        return new StoredTotalMessage(UUID.fromString(header.get(2)), Integer.parseInt(header.get(3)), Integer.parseInt(header.get(4)));
    }
}
