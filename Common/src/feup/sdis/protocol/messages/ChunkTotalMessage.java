package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Created by Afonso on 27/05/2016.
 */
public class ChunkTotalMessage extends ProtocolMessage {
    public ChunkTotalMessage(UUID fileId, int chunkNo) {
        super(Type.CHUNKTOTAL, Protocol.VERSION, fileId, chunkNo);
    }
}

