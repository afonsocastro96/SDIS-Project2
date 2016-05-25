package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

import java.util.UUID;

/**
 * Created by Afonso on 25/05/2016.
 */
public class WhoAmIMessage extends ProtocolMessage {
    public WhoAmIMessage(UUID senderId) {
        super(Type.WHOAMI, Protocol.VERSION, senderId, null);
    }
}
