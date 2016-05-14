package feup.sdis.protocol;

import java.util.Observer;

/**
 * Protocol Listener
 */
public abstract class BackupProtocol implements Observer {

    /**
     * Version of the protocol
     */
    protected final int VERSION = 1;

}