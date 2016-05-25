package feup.sdis.protocol.messages;

import feup.sdis.protocol.Protocol;

/**
 * Created by Afonso on 25/05/2016.
 */
public class OkMessage extends ProtocolMessage{

    public OkMessage() {
        super(Type.OK, Protocol.VERSION);
    }
}
