package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.database.DatabaseApi;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.network.SSLServer;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.initiators.GetChunkInitiator;
import feup.sdis.protocol.initiators.ProtocolInitiator;
import feup.sdis.protocol.initiators.PutChunkInitiator;
import feup.sdis.protocol.messages.ChunkMessage;
import feup.sdis.protocol.messages.StoredTotalMessage;
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
 * Get chunk listener
 */
public class GetChunkListener extends ProtocolListener {

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
            protocolMessage = new GetChunkParser().parse(message);
            Node.getLogger().log(Level.DEBUG, protocolMessage.getHeader());
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse GETCHUNK message. " + e.getMessage());
            return;
        }

        // Get the needed information
        final SSLServer server = Relay.getInstance().getServer();
        final SSLManager monitor = server.getConnection(host, port);
        if(monitor == null)
            return;
        final UUID fileId = protocolMessage.getFileId();
        if(fileId == null)
            return;
        final int chunkNo = protocolMessage.getChunkNo();
        if(chunkNo == -1)
            return;

        // Get peers with that chunk
        final int chunkId = DatabaseApi.getChunkId(fileId, chunkNo);
        if(chunkId == -1)
            return;
        final List<UUID> chunkPeers = DatabaseApi.getChunkPeers(chunkId);
        if(chunkPeers == null)
            return;

        // Create list with online peers with that chunk
        final List<SSLManager> peers = new ArrayList<>();
        SSLManager connectionMonitor;
        for(final UUID peer : chunkPeers) {
            connectionMonitor = server.getConnection(peer);
            if(connectionMonitor != null)
                peers.add(connectionMonitor);
        }
        Collections.shuffle(peers);

        // Get the chunk from one of the available peers
        ProtocolInitiator protocolInitiator = null;
        Thread protocolThread;
        for(final SSLManager peerMonitor : peers) {
            // Check if the peer is not the initiator
            if(peerMonitor.getChannel().getHost().equalsIgnoreCase(monitor.getChannel().getHost()))
                if(peerMonitor.getChannel().getPort() == monitor.getChannel().getPort())
                    continue;

            // Send the get chunk
            protocolInitiator = new GetChunkInitiator(peerMonitor, fileId, chunkNo);
            protocolThread = new Thread(protocolInitiator);
            protocolThread.start();
            while (protocolThread.isAlive())
                try {
                    protocolThread.join();
                } catch (InterruptedException ignored) {}
            if (!protocolInitiator.hasReceivedResponse())
                continue;
            break;
        }
        if(protocolInitiator == null)
            return;

        // Decrypt the chunk body
        final byte[] encodedKey = DatabaseApi.getSecretKey(fileId, chunkNo);
        if(encodedKey == null)
            return;
        final SecretKey secretKey = Security.recoverSecretKey(Protocol.ENCRYPT_ALGORITHM, encodedKey);
        final byte[] decryptedBody;
        try {
            byte[] body = protocolInitiator.getResponse().getBody();
            if(body == null)
                return;
            decryptedBody = Security.decrypt(Protocol.ENCRYPT_ALGORITHM, secretKey, body);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Node.getLogger().log(Level.FATAL, "Could not decrypt the body. " + e.getMessage());
            return;
        }

        // Send response to the sender
        try {
            monitor.write(new ChunkMessage(fileId, chunkNo, decryptedBody).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}