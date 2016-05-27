package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkMessage;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.ChunkParser;

import java.util.Observable;

/**
 * Created by Afonso on 26/05/2016.
 */
public class ChunkListener extends ProtocolListener{
    private boolean receivedResponse;
    private final String host;
    private final int port;

    public ChunkListener(final String host, final int port){
        this.receivedResponse = false;
        this.host = host;
        this.port = port;
    }

    public boolean hasReceivedResponse() {
        return receivedResponse;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;
        if(!(arg instanceof Object[]))
            return;

        final Object[] objects = (Object[]) arg;
        if(!(objects[0] instanceof String))
            return;
        if(!(objects[1] instanceof Integer))
            return;
        if(!(objects[2] instanceof byte[]))
            return;

        final String host = (String) objects[0];
        final int port = (Integer) objects[1];
        final byte[] message = (byte[]) objects[2];

        final ProtocolMessage protocolMessage;

        try {
            protocolMessage = new ChunkParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse CHUNK message. " + e.getMessage());
            return;
        }



    }
}
