package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.ChunkListener;
import feup.sdis.protocol.messages.GetChunkMessage;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by joaos on 14/05/2016.
 */
public class GetChunkInitiator extends ProtocolInitiator {

    UUID fileId;
    int chunkNo;

    public GetChunkInitiator(UUID fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        final GetChunkMessage message = new GetChunkMessage(fileId, chunkNo);
        final ChunkListener listener = new ChunkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort());

        try {
            Peer.getInstance().getMonitor().write(message.getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
            return;
        }

        Peer.getInstance().getMonitor().addObserver(listener);

        while(!listener.hasReceivedResponse()){
            if(getAttempts() >= MAX_ATTEMPTS)
                break;

            if(getRounds() == 0){
                sendMessage(message);
                increaseAttempts();
            }

            increaseRounds();
            if(getRounds() >= MAX_ROUNDS)
                resetRounds();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        Peer.getInstance().getMonitor().deleteObserver(listener);

        if(listener.hasReceivedResponse())
            Node.getLogger().log(Level.DEBUG, "Server has sent the chunk");
        else
            Node.getLogger().log(Level.FATAL, "Could not receive the chunk from the server.");

    }
}
