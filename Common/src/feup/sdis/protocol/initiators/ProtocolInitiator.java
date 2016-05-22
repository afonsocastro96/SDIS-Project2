package feup.sdis.protocol.initiators;

/**
 * Protocol Listener
 */
public abstract class ProtocolInitiator implements Runnable {
    int MAXCHUNKSIZE = 64000;
}