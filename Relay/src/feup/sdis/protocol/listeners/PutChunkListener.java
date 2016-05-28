package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.database.DatabaseApi;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.network.SSLServer;
import feup.sdis.protocol.Protocol;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.initiators.ProtocolInitiator;
import feup.sdis.protocol.initiators.PutChunkInitiator;
import feup.sdis.protocol.messages.StoredTotalMessage;
import feup.sdis.protocol.messages.parsers.PutChunkParser;
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
 * Put chunk listener
 */
public class PutChunkListener extends ProtocolListener {

    /**
     * Constructor of PutChunkListener
     */
    public PutChunkListener() {
        this.receivedResponse = true;
    }

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
            protocolMessage = new PutChunkParser().parse(message);
        } catch (MalformedMessageException e) {
            Node.getLogger().log(Level.DEBUG, "Failed to parse PUTCHUNK message. " + e.getMessage());
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
        final byte[] body = protocolMessage.getBody();
        if(body == null)
            return;
        final int minReplicas = protocolMessage.getMinReplicas();

        // Check if the file is known
        final UUID peer = server.getUUID(host, port);
        if(peer == null)
            return;
        if(!DatabaseApi.hasFile(peer, fileId))
            return;

        // Encrypt the chunk
        final byte[] encryptedBody;
        final SecretKey key;
        try {
            key = Security.generateSecretKey(Protocol.ENCRYPT_ALGORITHM);
            encryptedBody = Security.encrypt(Protocol.ENCRYPT_ALGORITHM, key, body);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            Node.getLogger().log(Level.FATAL, "Could not encrypt the body. " + e.getMessage());
            return;
        }

        // Save in the database and get its id
        if(!DatabaseApi.addChunk(fileId, chunkNo, minReplicas, key.getEncoded()))
            return;
        final int chunkId = DatabaseApi.getChunkId(fileId, chunkNo);
        if(chunkId == -1)
            return;

        // Get current connections
        final List<SSLManager> peers = new ArrayList<>(server.getConnections());
        Collections.shuffle(peers);

        // Send to different peers
        int replicationDegree = 0;
        ProtocolInitiator protocolInitiator;
        Thread protocolThread;
        UUID peerUUID;

        for(final SSLManager peerMonitor : peers) {
            if(replicationDegree >= minReplicas)
                break;

            // Check if the peer is not the initiator
            if(peerMonitor.getChannel().getHost().equalsIgnoreCase(monitor.getChannel().getHost()))
                if(peerMonitor.getChannel().getPort() == monitor.getChannel().getPort())
                    continue;

            // Send the chunk
            protocolInitiator = new PutChunkInitiator(peerMonitor, fileId, chunkNo, encryptedBody);
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
            if(!DatabaseApi.addChunkReplica(peerUUID, chunkId))
                continue;

            replicationDegree++;
        }

        // Send response to the sender
        try {
            monitor.write(new StoredTotalMessage(fileId, chunkNo, replicationDegree).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}
