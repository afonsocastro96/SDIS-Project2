package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.WhoAmIMessage;

import java.util.UUID;

/**
 * Created by Afonso on 25/05/2016.
 */
public class WhoAmIParser extends ProtocolParser {
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if (header.size() != 3){
            throw new MalformedMessageException("Wrong number of arguments for the WHOAMI message: 3 arguments must be present");
        }

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.FILENAME.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if(!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate sender ID */
        if (!validSenderId(header.get(2)))
            throw new MalformedMessageException("Sender ID must be an UUID");

        return new WhoAmIMessage(UUID.fromString(header.get(2)));
    }
}
