package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.ProtocolMessage;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.PutChunkMessage;

import java.util.UUID;

/**
 * Put chunk parser
 */
public class PutChunkParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if (header.size() != 6)
            throw new MalformedMessageException("Wrong number of arguments for the PUTCHUNK message: 5 arguments must be present");

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.PUTCHUNK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        if (!validSenderId(header.get(2)))
            throw new MalformedMessageException("Sender ID must be an UUID");

        /* Validate file ID */
        if (!validFileId(header.get(3))){
            throw new MalformedMessageException("File ID must be an UUID");
        }

            /* Validate chunk No */
        if (!validChunkNo(header.get(4))){
            throw new MalformedMessageException("Chunk Number must be an integer smaller than 1000000");
        }

            /* Validate Replication Deg */
        if(!validReplicationDeg(header.get(5))){
            throw new MalformedMessageException("Replication Degree must be a single digit");
        }

        return new PutChunkMessage(UUID.fromString(header.get(2)), UUID.fromString(header.get(3)), Integer.parseInt(header.get(4)), Integer.parseInt(header.get(5)), body);
    }
}
