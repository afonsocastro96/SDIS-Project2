package outdated.general;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Scanner;

public class SpaceMetadataManager {
    private static SpaceMetadataManager instance = null;
    private static final String SPACE_METADATA_FILENAME = "space.metadata";
    private File file;
    public static final long MAX_SPACE = 10 * 1024 * 1024;
    private long reclaimedSpace;

    public static SpaceMetadataManager getInstance(){
        if(instance == null)
            instance = new SpaceMetadataManager();
        return instance;
    }

    private SpaceMetadataManager() {

    }

    public void init(String localId) throws IOException {
        Path path = FileSystems.getDefault().getPath("peer" + localId, SPACE_METADATA_FILENAME);
        file = path.toFile();

        if (!file.exists()) {
            file.createNewFile();
            setReclaimedSpace(0);
            save();
        }
        load();
    }

    private void load() throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
        this.reclaimedSpace = scanner.nextLong();
    }

    private void save() throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bw.write("" + this.reclaimedSpace);
        bw.flush();
        bw.close();
    }

    public long getAvailableSpace() {
        return MAX_SPACE - reclaimedSpace;
    }

    public long getReclaimedSpace() {
        return reclaimedSpace;
    }

    public void setReclaimedSpace(long reclaimedSpace) throws IOException {
        this.reclaimedSpace = Long.min(MAX_SPACE, reclaimedSpace);
        save();
    }
}
