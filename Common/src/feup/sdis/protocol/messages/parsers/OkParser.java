package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.StringJoiner;

/**
 * Ok parser
 */
public class OkParser extends ProtocolParser{

    /**
     * Parse a message
     * @param message message to be parsed
     * @return parsed protocol message
     * @throws MalformedMessageException when message is malformed
     */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);
        if (header.size() < 3)
            throw new MalformedMessageException("Wrong number of arguments for the OK message: must have at least 3 arguments");

        /* Validate protocol */
        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.OK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if (!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        // Create the message that corresponds this OK
        final StringJoiner sj = new StringJoiner(" ");
        for(int i = 2; i < header.size(); i++){
            sj.add(header.get(i));
        }

        return new OkMessage(sj.toString());
    }
}
