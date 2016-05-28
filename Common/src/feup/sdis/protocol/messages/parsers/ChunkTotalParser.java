package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkTotalMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.UUID;

/**
 * Created by Afonso on 27/05/2016.
 */
public class ChunkTotalParser extends ProtocolParser {
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if(header.size() != 3)
            throw new MalformedMessageException("Wrong number of arguments for the CHUNKTOTAL message: 4 arguments must be present");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.RESTORE.toString()))
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

        return new ChunkTotalMessage(UUID.fromString(header.get(2)), Integer.parseInt(header.get(3)));
    }
}
