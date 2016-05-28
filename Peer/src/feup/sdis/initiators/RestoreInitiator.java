package feup.sdis.initiators;

import feup.sdis.protocol.listeners.ChunkTotalListener;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.RestoreMessage;

/**
 * Created by Afonso on 27/05/2016.
 */
public class RestoreInitiator extends ProtocolInitiator {
    public RestoreInitiator(String fileName){
        message = new RestoreMessage(fileName);
        listener = new ChunkTotalListener();
    }
}
