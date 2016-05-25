package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Who am I message
 */
public class WhoAmIMessage extends ProtocolMessage {

    /**
     * UUID of the sender
     */
    private final UUID senderId;

    /**
     * Constructor of WhoAmIMessage
     * @param senderId id of the sender
     */
    public WhoAmIMessage(final UUID senderId) {
        super(Type.WHOAMI, Protocol.VERSION);
        this.senderId = senderId;
    }

    /**
     * Get the header of the message
     * @return header of the message
     */
    @Override
    public String getHeader(){
        StringJoiner sj = new StringJoiner(" ", "", CRLF);
        sj.add(getMessageType().toString())
                .add("" + getVersion())
                .add("" + senderId);

        return sj.toString();
    }
}
