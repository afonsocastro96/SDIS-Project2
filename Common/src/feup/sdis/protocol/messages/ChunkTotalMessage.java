package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Chunk total message
 */
public class ChunkTotalMessage extends ProtocolMessage {

    /**
     * File name to get the total chunk
     */
    private final String fileName;

    /**
     * Constructor of ChunkTotalMessage
     * @param fileId file id to get the total number of chunks
     * @param numberChunks number of chunks
     * @param fileName file name to get the total number of chunks
     */
    public ChunkTotalMessage(final UUID fileId, final int numberChunks, final String fileName) {
        super(Type.CHUNKTOTAL, Protocol.VERSION, fileId, numberChunks);
        this.fileName = fileName;
    }

    /**
     * Get the file name
     * @return file name
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
                .add("" + getChunkNo())
                .add("" + fileName);

        return sj.toString();
    }
}

