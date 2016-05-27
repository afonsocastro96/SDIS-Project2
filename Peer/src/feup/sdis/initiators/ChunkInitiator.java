package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.messages.ChunkMessage;

/**
 * Created by joaos on 14/05/2016.
 */
public class ChunkInitiator extends ProtocolInitiator {

    int chunkNo;
    byte[] body;

    public ChunkInitiator(int chunkNo, byte[] body){
        this.chunkNo = chunkNo;
        this.body = body;
    }

    @Override
    public void run() {
        final ChunkMessage message = new ChunkMessage(Peer.getInstance().getId(), chunkNo, body);
        sendMessage(message);
        Node.getLogger().log(Level.DEBUG, "Sent a chunk to the server.");

    }
}
