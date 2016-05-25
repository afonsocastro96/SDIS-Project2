package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * File name message
 */
public class FileNameMessage extends ProtocolMessage {
    /**
     * The file to transfer's name.
     */
    private String fileName;

    /**
     * FileNameMessage constructor
     * @param fileId id of file to send.
     * @param fileName name of file.
     */
    public FileNameMessage(UUID fileId, String fileName){
        super(Type.FILENAME, Protocol.VERSION, fileId);
        this.fileName = fileName;
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
                .add("" + getFileId())
                .add("" + fileName);

        return sj.toString();
    }
}
