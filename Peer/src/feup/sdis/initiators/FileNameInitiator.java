package feup.sdis.initiators;


import feup.sdis.Peer;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.FileNameMessage;

import java.util.UUID;

/**
 * File name initiator
 */
public class FileNameInitiator extends ProtocolInitiator {

    /**
     * Id of the file
     */
    private final UUID fileId;

    /**
     * File name
     */
    private final String fileName;

    /**
     * Constructor of FileNameInitiator
     * @param fileId id of the file
     * @param fileName name of the file
     */
    public FileNameInitiator(final UUID fileId, final String fileName){
        this.fileId = fileId;
        this.fileName = fileName;

        message = new FileNameMessage(this.fileId, this.fileName);
        listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
    }

    /**
     * Get the file id
     * @return file id
     */
    public UUID getFileId() {
        return fileId;
    }
}
