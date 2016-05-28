package feup.sdis.initiators;

import feup.sdis.protocol.listeners.ChunkTotalListener;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.RestoreMessage;

/**
 * Restore initiator
 */
public class RestoreInitiator extends ProtocolInitiator {

    /**
     * Constructor of RestoreInitiator
     * @param fileName path of the file to be restored
     */
    public RestoreInitiator(final String fileName){
        message = new RestoreMessage(fileName);
        listener = new ChunkTotalListener();
    }
}
