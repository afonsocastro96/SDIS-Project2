package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;

/**
 * Created by Afonso on 27/05/2016.
 */
public class RestoreMessage extends ProtocolMessage {
    private String fileName;

    public RestoreMessage(String fileName) {
        super(Type.RESTORE, Protocol.VERSION);
        this.fileName = fileName;
    }

    /**
     * Get the header of the message
     * @return header of the message
     */
    @Override
    public String getHeader(){
        StringJoiner sj = new StringJoiner(" ");
        sj.add(getMessageType().toString())
                .add("" + getVersion())
                .add("" + getFileId())
                .add("" + fileName);

        return sj.toString();
    }
}
