package feup.sdis.protocol.initiators;

import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.DeleteMessage;

/**
 * Delete initiator
 */
public class DeleteInitiator extends ProtocolInitiator {

    /**
     * Constructor of DeleteInitiator
     * @param monitor monitor of this channel
     * @param fileName path of the file to be restored
     */
    public DeleteInitiator(final SSLManager monitor, final String fileName){
        super(monitor);

        message = new DeleteMessage(fileName.replaceAll(" ", "%20"));
        listener = new OkListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                message.getHeader());
    }
}
