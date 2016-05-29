package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.parsers.DeleteParser;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.UUID;

/**
 * Delete listener
 */
public class DeleteListener extends ProtocolListener {

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
            protocolMessage = new DeleteParser().parse(message);
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse DELETE message. " + e.getMessage());
            return;
        }

        // Get needed information
        final SSLManager monitor = Peer.getInstance().getMonitor();
        final UUID fileId = protocolMessage.getFileId();
        if(fileId == null)
            return;

        // Get file directory
        final File fileDir = new File(fileId.toString());
        if(!fileDir.exists())
            return;

        // Delete all files
        final File[] files = fileDir.listFiles();
        for (final File f: files) f.delete();
        fileDir.delete();

        // Send response to the sender
        try {
            monitor.write(new OkMessage(protocolMessage.getHeader()).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}
