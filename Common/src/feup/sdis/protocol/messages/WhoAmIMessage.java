package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Created by Afonso on 25/05/2016.
 */
public class WhoAmIMessage extends ProtocolMessage {
    UUID senderId;
    
    public WhoAmIMessage(UUID senderId) {
        super(Type.WHOAMI, Protocol.VERSION);
        this.senderId = senderId;
    }

    @Override
    public String getHeader(){
        StringJoiner sj = new StringJoiner(" ", "", CRLF);
        sj.add(getMessageType().toString())
                .add("" + getVersion())
                .add("" + senderId);

        return sj.toString();
    }
}
