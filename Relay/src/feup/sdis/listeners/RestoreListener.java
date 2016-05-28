package feup.sdis.listeners;

import feup.sdis.Node;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.listeners.ProtocolListener;
import feup.sdis.protocol.messages.ProtocolMessage;
import feup.sdis.protocol.messages.parsers.RestoreParser;

import java.util.Observable;

/**
 * Created by Afonso on 27/05/2016.
 */
public class RestoreListener extends ProtocolListener{
    /**
     * Called when a new message is received
     * @param o object that was being observed
     * @param arg argument of the notification
     */
    @Override
    public void update(Observable o, Object arg) {
        if(!(o instanceof SSLManager))
            return;
        if(!(arg instanceof Object[]))
            return;

        // Validate data type of the objects
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
        try{
            protocolMessage = new RestoreParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e){
            Node.getLogger().log(Level.DEBUG, "Failed to parse RESTORE message. " + e.getMessage());
            return;
        }
    }
}
