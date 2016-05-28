package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.database.DatabaseApi;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.network.SSLServer;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.WhoAmIMessage;
import feup.sdis.protocol.messages.parsers.WhoAmIParser;

import java.io.IOException;
import java.util.Observable;
import java.util.UUID;

/**
 * Who am I listener
 */
public class WhoAmIListener extends ProtocolListener {

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

        // Validate message
        try {
            protocolMessage = new WhoAmIParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse WHOAMI message. " + e.getMessage());
            return;
        }

        // Get needed instances
        final SSLServer server = Relay.getInstance().getServer();
        final SSLManager monitor = server.getConnection(host, port);
        if(monitor == null)
            return;

        final UUID senderId = ((WhoAmIMessage) protocolMessage).getSenderId();

        // Save in the database
        if(!DatabaseApi.hasPeer(senderId))
            if(!DatabaseApi.addPeer(senderId))
                return;

        // Save the UUID
        server.setUUID(host, port, senderId);

        // Send response to the sender
        try {
            monitor.write(new OkMessage(protocolMessage.getHeader()).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}
