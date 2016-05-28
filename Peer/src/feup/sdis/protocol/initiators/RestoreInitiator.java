package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.ChunkTotalListener;
import feup.sdis.protocol.messages.RestoreMessage;

/**
 * Restore initiator
 */
public class RestoreInitiator extends ProtocolInitiator {

    /**
     * Constructor of RestoreInitiator
     * @param monitor monitor of this channel
     * @param fileName path of the file to be restored
     */
    public RestoreInitiator(final SSLManager monitor, final String fileName){
        super(monitor);
        message = new RestoreMessage(fileName.replaceAll(" ", "%20"));
        listener = new ChunkTotalListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                fileName.replaceAll(" ", "%20"));
    }
}
