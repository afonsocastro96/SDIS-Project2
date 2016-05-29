package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Delete message
 */
public class DeleteMessage extends ProtocolMessage {

    /**
     * Name of the file to be restored
     */
    private String fileName;

    /**
     * Constructor of DeleteMessage
     * @param fileName name of the file to be restored
     */
    public DeleteMessage(final String fileName) {
        super(Type.DELETE, Protocol.VERSION, (fileName.split("-").length == 5 ? UUID.fromString(fileName) : null));
        this.fileName = fileName;
    }

    /**
     * Get the file name to be restored
     * @return file name to be restored
     */
    public String getFileName() {
        return fileName;
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
                .add("" + fileName);

        return sj.toString();
    }
}
