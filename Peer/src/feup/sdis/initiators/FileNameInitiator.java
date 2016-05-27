package feup.sdis.initiators;


import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.listeners.OkListener;
import feup.sdis.protocol.messages.FileNameMessage;

import java.io.IOException;

/**
 * Created by Afonso on 26/05/2016.
 */
public class FileNameInitiator extends ProtocolInitiator {

    private final String fileName;

    public FileNameInitiator(String fileName){
        this.fileName = fileName;
    }

    @Override
    public void run() {
        final FileNameMessage message = new FileNameMessage(Peer.getInstance().getId(), fileName);

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
