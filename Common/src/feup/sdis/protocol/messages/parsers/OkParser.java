package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.StringJoiner;

/**
 * Created by Afonso on 25/05/2016.
 */
public class OkParser extends ProtocolParser{
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);
        if (header.size() < 3)
            throw new MalformedMessageException("Wrong number of arguments for the OK message: must have at least 3 arguments");

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.OK.toString()))
            throw new MalformedMessageException("Wrong protocol");

        StringJoiner sj = new StringJoiner(" ");
        for(int i = 2; i < header.size(); i++){
            sj.add(header.get(i));
        }

        return new OkMessage(sj.toString());
    }
}
