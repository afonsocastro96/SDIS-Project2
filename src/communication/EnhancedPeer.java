package communication;

import subprotocol.*;

import java.io.IOException;
import java.rmi.RemoteException;


public class EnhancedPeer extends Peer {
    public EnhancedPeer(int id, String mcAddress, int mcPort, String mdbAddress, int mdbPort, String mdrAddress, int mdrPort) throws IOException {
        super(id, mcAddress, mcPort, mdbAddress, mdbPort, mdrAddress, mdrPort);
    }

    public void start() {

        try {
            registerRMI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        startMulticastChannelListeners();

        new EnhancedPutChunkListener("" + localId, mcChannel, mdbChannel).start();
        new StoredListener(""+localId, mcChannel).start();
        new DeleteListener(""+localId, mcChannel).start();
        new DeleteEnhListener("" + localId, mcChannel).start();
        new GetChunkListener(""+localId, mcChannel, mdrChannel).start();
        new RemovedListener(""+localId, mcChannel, mdbChannel).start();
    }


    @Override
    public String backupEnh(String filepath, int replicationDeg) throws RemoteException
    {
        return "BACKUPENH protocol is implemented by non-iniator peers by not storing chunks whose replication degree is above the desired.\n" +
                backup(filepath, replicationDeg);
    }

    public String restoreEnh(String filepath) throws RemoteException
    {
        return "Not implemented";
    }

    public String deleteEnh(String filepath) throws RemoteException
    {
        return "DELETEENH subprotocol will search for messages about deleted files and send a DELETE message again to the system.\n"
                + delete(filepath);
    }

    public String reclaimEnh(long space) throws RemoteException {
        return "Not implemented";
    }
}
