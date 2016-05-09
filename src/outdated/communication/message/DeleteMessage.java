package outdated.communication.message;

import outdated.communication.Message;
import outdated.communication.MessageParser;
import outdated.general.MalformedMessageException;

import java.io.IOException;


public class DeleteMessage extends Message {

    private final static String IDENTIFIER = "DELETE";
    private final static String VERSION = "1.0";

    public static class Parser extends MessageParser {
        @Override
        public Message parse(byte[] messageBytes) throws IOException, MalformedMessageException {
            splitMessage(messageBytes);

            if (header.size() != 1)
                throw new MalformedMessageException("Wrong number of lines for outdated.subprotocol " + IDENTIFIER + " version " + VERSION);

            String[] headerLine = header.get(0);

            if (headerLine.length != 4)
                throw new MalformedMessageException("Wrong number of arguments for the DELETE protocol: 3 arguments must be present");

            if (!headerLine[0].equalsIgnoreCase(IDENTIFIER))
                throw new MalformedMessageException("Wrong protocol");

            /* Validate version */
            if (!headerLine[1].equalsIgnoreCase(VERSION))
                throw new MalformedMessageException("Version must follow the following format: <n>.<m>");

            if (!validSenderId(headerLine[2]))
                throw new MalformedMessageException("Sender ID must be an Integer");

            /* Validate file ID */
            if (!validFileId(headerLine[3])){
                throw new MalformedMessageException("Metadata ID must be hashed with the SHA-256 cryptographic function");
            }

            if (body.length != 0){
                throw new MalformedMessageException("There must be no body present");
            }

            return new DeleteMessage(headerLine[2], headerLine[3]);
        }
    }

    public DeleteMessage(String senderId, String fileId) {
        super(IDENTIFIER, VERSION, senderId, fileId, "", "", new byte[]{});
    }
}
