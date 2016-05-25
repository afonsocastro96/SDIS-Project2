package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;

/**
 * Ok message
 */
public class OkMessage extends ProtocolMessage{

    /**
     * Request message for the OK
     */
    final String message;

    /**
     * Constructor of OKMessage
     * @param message request message for the OK
     */
    public OkMessage(final String message) {
        super(Type.OK, Protocol.VERSION);
        this.message = message;
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
                .add("" + message);

        return sj.toString();
    }
}
