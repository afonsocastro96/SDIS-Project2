package feup.sdis.protocol.messages.parsers;

import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.FileNameMessage;
import feup.sdis.protocol.messages.ProtocolMessage;

import java.util.UUID;

/**
 * Created by Afonso on 24/05/2016.
 */
public class FileNameParser extends ProtocolParser {
    /* FILENAME version senderID fileID fileName */
    @Override
    public ProtocolMessage parse(byte[] message) throws MalformedMessageException {
        splitMessage(message);
        if (header.size() != 5){
            throw new MalformedMessageException("Wrong number of arguments for the FILENAME message: 5 arguments must be present");
        }

        if (!header.get(0).equalsIgnoreCase(ProtocolMessage.Type.FILENAME.toString()))
            throw new MalformedMessageException("Wrong protocol");

        /* Validate version */
        if(!validVersion(header.get(1)))
            throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

        /* Validate sender ID */
        if (!validSenderId(header.get(2)))
            throw new MalformedMessageException("Sender ID must be an UUID");

        /* Validate file ID */
        if (!validFileId(header.get(3)))
            throw new MalformedMessageException("File ID must be an UUID");

        /* Validate file name */
        if(!validFileName(header.get(4)))
            throw new MalformedMessageException("File name must have between 1 and 256 characters");

        return new FileNameMessage(UUID.fromString(header.get(2)), UUID.fromString(header.get(3)), header.get(4));
    }
}
