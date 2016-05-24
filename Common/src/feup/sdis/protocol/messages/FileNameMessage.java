package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;
import java.util.UUID;

/**
 * Created by Afonso on 23/05/2016.
 */
public class FileNameMessage extends ProtocolMessage {
    /**
     * The file to transfer's name.
     */
    private String fileName;

    /**
     * FileNameMessage constructor
     * @param senderId id of sender.
     * @param fileId id of file to send.
     * @param fileName name of file.
     */
    public FileNameMessage(UUID senderId, UUID fileId, String fileName){
        super(Type.FILENAME, Protocol.VERSION, senderId, fileId);
        this.fileName = fileName;
    }
}
