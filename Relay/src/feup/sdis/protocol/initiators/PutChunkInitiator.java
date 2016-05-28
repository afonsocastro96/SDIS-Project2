package feup.sdis.protocol.initiators;


import feup.sdis.network.SSLManager;
import feup.sdis.protocol.listeners.StoredListener;
import feup.sdis.protocol.messages.PutChunkMessage;

import java.util.UUID;

/**
 * Put chunk initiator
 */
public class PutChunkInitiator extends ProtocolInitiator {

    /**
     * Id of the file of the chunk
     */
    private final UUID fileId;

    /**
     * Number of the chunk to send
     */
    private final int chunkNo;

    /**
     * Body of the chunk
     */
    private final byte[] body;

    /**
     * Constructor of PutChunkInitiator
     * @param monitor monitor for this initiator
     * @param fileId id of the file
     * @param chunkNo number of the chunk to put
     * @param body body of the message
     */
    public PutChunkInitiator(final SSLManager monitor, final UUID fileId, final int chunkNo, final byte[] body){
        super(monitor);

        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.body = body;

        message = new PutChunkMessage(fileId, chunkNo, 1, body);
        listener = new StoredListener(
                monitor.getChannel().getHost(),
                monitor.getChannel().getPort(),
                this.fileId,
                this.chunkNo);
    }
}
