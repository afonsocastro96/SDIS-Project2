package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.WhoAmIMessage;

import java.util.UUID;

/**
 * Who am I parser
 */
public class WhoAmIParser extends ProtocolParser {

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);

        if (header.size() != 3){
            throw new MalformedMessageException("Wrong number of arguments for the WHOAMI message: 3 arguments must be present");
        }

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.WHOAMI.toString()))
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
