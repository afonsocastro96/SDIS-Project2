package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;

/**
 * Created by Afonso on 25/05/2016.
 */
public class OkMessage extends ProtocolMessage{

    final String message;

    public OkMessage(final String message) {
        super(Type.OK, Protocol.VERSION);
        this.message = message;
    }

    @Override
    public String getHeader(){
        StringJoiner sj = new StringJoiner(" ", "", CRLF);
        sj.add(getMessageType().toString())
                .add("" + getVersion())
                .add("" + message);

        return sj.toString();
    }
}
