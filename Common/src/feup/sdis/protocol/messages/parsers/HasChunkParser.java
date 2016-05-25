package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.HasChunkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.UUID;

/**
 * Created by Afonso on 25/05/2016.
 */
public class HasChunkParser extends ProtocolParser {
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if(header.size() != 4)
            throw new MalformedMessageException("Wrong number of arguments for the HASCHUNK message: 4 arguments must be present");

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.HASCHUNK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate sender ID */
        if (!validSenderId(header.get(2)))
            throw new MalformedMessageException("Sender ID must be an UUID");

        /* Validate file ID */
        if (!validFileId(header.get(3))){
            throw new MalformedMessageException("File ID must be an UUID");
        }

        return new HasChunkMessage(UUID.fromString(header.get(2)), UUID.fromString(header.get(3)));
    }
}
