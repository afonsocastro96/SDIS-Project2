package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.database.DatabaseApi;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.network.SSLServer;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.ChunkTotalMessage;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.RestoreMessage;
import feup.sdis.protocol.messages.parsers.HasChunkParser;
import feup.sdis.protocol.messages.parsers.RestoreParser;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

/**
 * Has chunk listener
 */
public class HasChunkListener extends ProtocolListener{

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

        try{
            protocolMessage = new HasChunkParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e){
            Node.getLogger().log(Level.DEBUG, "Failed to parse HASCHUNK message. " + e.getMessage());
            return;
        }

        // Get the needed information
        final SSLServer server = Relay.getInstance().getServer();
        final SSLManager monitor = server.getConnection(host, port);
        if(monitor == null)
            return;

        final UUID peer = server.getUUID(host, port);
        if(peer == null)
            return;
        final UUID fileId = protocolMessage.getFileId();
        if(fileId == null)
            return;
        final int chunkNo = protocolMessage.getChunkNo();
        if(chunkNo == -1)
            return;

        // Check if chunk is in the system
        if(!DatabaseApi.hasChunk(fileId, chunkNo))
            return;

        // Check if peer is supposed to own the chunk
        final int chunkId = DatabaseApi.getChunkId(fileId, chunkNo);
        if(chunkId == -1)
            return;
        if(!DatabaseApi.hasChunkReplica(peer, chunkId))
            return;

        // Send response to the sender
        try {
            monitor.write(new OkMessage(protocolMessage.getHeader()).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}
