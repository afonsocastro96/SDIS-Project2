package feup.sdis.initiators;

import feup.sdis.Node;
import feup.sdis.Peer;
import feup.sdis.logger.Level;
import feup.sdis.protocol.messages.PutChunkMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by joaos on 14/05/2016.
 */
public class PutChunkInitiator extends ProtocolInitiator {

    String filePath;
    int replicationDeg;
    int totalChunks;

    public PutChunkInitiator(final String filePath, final int replicationDegree){
        this.filePath = filePath;
        this.replicationDeg = replicationDegree;
        long fileSize = new File(filePath).length();
        this.totalChunks = (int)(fileSize / MAXCHUNKSIZE) + 1;
    }

    @Override
    public void run() {
        try {
            RandomAccessFile in = new RandomAccessFile(filePath, "r");
            byte[] buffer = new byte[MAXCHUNKSIZE];
            for (int chunkNo = 0; chunkNo < totalChunks; ++chunkNo) {
                in.seek(chunkNo * MAXCHUNKSIZE);
                int size = in.read(buffer);
                PutChunkMessage message = new PutChunkMessage(UUID.randomUUID(), chunkNo, replicationDeg, buffer);
                try {
                    Peer.getInstance().getMonitor().write(message.getBytes());
                } catch (IOException e) {
                    Node.getLogger().log(Level.ERROR, "Could not send the message. " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
