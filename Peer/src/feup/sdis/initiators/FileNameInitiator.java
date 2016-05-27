package feup.sdis.initiators;


import feup.sdis.Peer;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.FileNameMessage;

/**
 * File name initiator
 */
public class FileNameInitiator extends ProtocolInitiator {

    /**
     * File name
     */
    private final String fileName;

    /**
     * Constructor of FileNameInitiator
     * @param fileName name of the file
     */
    public FileNameInitiator(String fileName){
        this.fileName = fileName;

        message = new FileNameMessage(Peer.getInstance().getId(), fileName);
        listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
    }
}
