package feup.sdis.protocol.listeners;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLManager;
import feup.sdis.protocol.exceptions.MalformedMessageException;
import feup.sdis.protocol.messages.StoredMessage;
import feup.sdis.protocol.messages.parsers.PutChunkParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.UUID;

/**
 * Put chunk listener
 */
public class PutChunkListener extends ProtocolListener {

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

        // Get needed information
        final SSLManager monitor = Peer.getInstance().getMonitor();
        final UUID fileId = protocolMessage.getFileId();
        if(fileId == null)
            return;
        final int chunkNo = protocolMessage.getChunkNo();
        if(chunkNo == -1)
            return;
        final byte[] body = protocolMessage.getBody();
        if(body == null)
            return;

        // Create file directory
        final File fileDir = new File("data" + File.separator + fileId.toString());
        if(!fileDir.exists())
            if(!fileDir.mkdirs())
                return;

        // TODO save checksum

        // Create chunk file
        final File chunkFile = new File(fileDir.getAbsolutePath() + File.separator + chunkNo + ".bin");
        if(!chunkFile.exists())
            try {
                if(!chunkFile.createNewFile())
                    return;
            } catch (IOException e) {
                Node.getLogger().log(Level.FATAL, "Could not create the chunk number " + chunkNo + " of the file " + fileId + ". " + e.getMessage());
                return;
            }

        // Write the chunk to the file
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
            fileOutputStream.write(body);
            fileOutputStream.close();
        } catch (IOException e) {
            Node.getLogger().log(Level.FATAL, "Could not write the chunk number " + chunkNo + " of the file " + fileId + ". " + e.getMessage());
            return;
        }

        // Send response to the sender
        try {
            monitor.write(new StoredMessage(fileId, chunkNo).getBytes());
        } catch (IOException e) {
            Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
        }
    }
}
