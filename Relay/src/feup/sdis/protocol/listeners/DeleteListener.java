package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.database.DatabaseApi;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.network.SSLServer;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.initiators.DeleteInitiator;
import feup.sdis.protocol.initiators.ProtocolInitiator;
import feup.sdis.protocol.messages.ChunkMessage;
import feup.sdis.protocol.messages.DeleteMessage;
import feup.sdis.protocol.messages.OkMessage;
import feup.sdis.protocol.messages.RestoreMessage;
import feup.sdis.protocol.messages.parsers.DeleteParser;
import feup.sdis.protocol.messages.parsers.GetChunkParser;
import feup.sdis.utils.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse DELETE message. " + e.getMessage());
            return;
        }

        // Get the needed information
        final SSLServer server = Relay.getInstance().getServer();
        final SSLManager monitor = server.getConnection(host, port);
        if(monitor == null)
            return;

        final String fileName = ((DeleteMessage) protocolMessage).getFileName().replaceAll("%20", " ");
        if(fileName == null)
            return;
        final UUID peer = server.getUUID(host, port);
        if(peer == null)
            return;
        final UUID fileId = DatabaseApi.getFileId(peer, fileName);
        if(fileId == null)
            return;

        // Get peers with that at least one chunk of the file
        Node.getLogger().log(Level.DEBUG, "Getting peers that has at least a chunk of that file " + fileId.toString() + ".");
        final List<UUID> peersFile = DatabaseApi.getPeersFile(fileId);
        if(peersFile == null)
            return;

        // Create list with online peers with at least one chunk of that chunk
        final List<SSLManager> peers = new ArrayList<>();
        SSLManager connectionMonitor;
        UUID peerUUID;
        for(final UUID chunkPeer : peersFile) {
            connectionMonitor = server.getConnection(chunkPeer);
            if(connectionMonitor != null)
                peers.add(connectionMonitor);
        }

        // Get the chunk from one of the available peers
        ProtocolInitiator protocolInitiator = null;
        Thread protocolThread;
        for(final SSLManager peerMonitor : peers) {
            // Check if the peer is not the initiator
            if(peerMonitor.getChannel().getHost().equalsIgnoreCase(monitor.getChannel().getHost()))
                if(peerMonitor.getChannel().getPort() == monitor.getChannel().getPort())
                    continue;

            // Send the get chunk
            protocolInitiator = new DeleteInitiator(peerMonitor, fileId.toString());
            protocolThread = new Thread(protocolInitiator);
            protocolThread.start();
            while (protocolThread.isAlive())
                try {
                    protocolThread.join();
                } catch (InterruptedException ignored) {}
            if (!protocolInitiator.hasReceivedResponse())
                continue;

            // Save in the database
            peerUUID = server.getUUID(peerMonitor.getChannel().getHost(), peerMonitor.getChannel().getPort());
            if(peerUUID == null)
                continue;

            break;
        }
        if(protocolInitiator == null)
            return;

        // Delete from database
        if(!DatabaseApi.removeFile(fileId))
            return;

        // Send response to the sender
        try {
            monitor.write(new OkMessage(protocolMessage.getHeader()).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}