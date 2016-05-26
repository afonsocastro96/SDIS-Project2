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

        try {
            Peer.getInstance().getMonitor().write(message.getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
            return;
        }

        final OkListener listener = new OkListener(
                Peer.getInstance().getMonitor().getChannel().getHost(),
                Peer.getInstance().getMonitor().getChannel().getPort(),
                message.getHeader());
        Peer.getInstance().getMonitor().addObserver(listener);

        while(!listener.hasReceivedResponse()){
            try{
                Thread.sleep(1000);
            } catch(InterruptedException ignored){}
        }

        Peer.getInstance().getMonitor().deleteObserver(listener);
        Node.getLogger().log(Level.INFO, "Server has received the file's name");
    }
}
