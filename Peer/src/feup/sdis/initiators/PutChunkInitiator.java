package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.PutChunkMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by joaos on 14/05/2016.
 */
public class PutChunkInitiator extends ProtocolInitiator {

    private int chunkNo;
    private int replicationDegree;
    private byte[] body;

    public PutChunkInitiator(final int chunkNo, final int replicationDegree, final byte[] body){
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = body;
    }

    @Override
    public void run() {
        final PutChunkMessage message = new PutChunkMessage(Peer.getInstance().getId(), chunkNo, replicationDegree, body);
        final OkListener listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());

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
            Node.getLogger().log(Level.DEBUG, "Server has received the chunk");
        else
            Node.getLogger().log(Level.FATAL, "Could not send the chunk to the server.");

    }
}
