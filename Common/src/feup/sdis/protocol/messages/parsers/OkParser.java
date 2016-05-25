package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

/**
 * Created by Afonso on 25/05/2016.
 */
public class OkParser extends ProtocolParser{
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);
        if (header.size() != 1)
            throw new MalformedMessageException("Wrong number of arguments for the OK message: 1 argument must be present");

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.OK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        return new OkMessage();
    }
}
