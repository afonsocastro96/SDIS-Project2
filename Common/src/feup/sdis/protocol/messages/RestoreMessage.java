package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;

/**
 * Restore message
 */
public class RestoreMessage extends ProtocolMessage {

    /**
     * Name of the file to be restored
     */
    private String fileName;

    /**
     * Constructor of RestoreMessage
     * @param fileName name of the file to be restored
     */
    public RestoreMessage(String fileName) {
        super(Type.RESTORE, Protocol.VERSION);
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
                .add("" + getFileId())
                .add("" + fileName);

        return sj.toString();
    }
}
