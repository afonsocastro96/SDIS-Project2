package feup.sdis.protocol.initiators;


import feup.sdis.network.SSLManager;
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
     * @param monitor monitor of this initiator
     * @param fileId id of the file
     * @param fileName name of the file
     */
    public FileNameInitiator(final SSLManager monitor, final UUID fileId, final String fileName){
        super(monitor);
        this.fileId = fileId;
        this.fileName = fileName;

        message = new FileNameMessage(this.fileId, this.fileName.replaceAll(" ", "%20"));
        listener = new OkListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
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
