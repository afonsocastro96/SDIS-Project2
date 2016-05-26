package feup.sdis.database;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.logger.Level;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Database queries
 */
public class DatabaseApi {

    /*
        PEERS TABLE
     */

    /**
     * Check if the system has already acknowledge of a given peer
     * @param serialNumber serial number of that peer
     * @return true if has, false otherwise
     */
    public static boolean hasPeer(final UUID serialNumber) {
        final String query = "SELECT serial_number FROM peers WHERE serial_number = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};

        return hasResult(query, params);
    }

    /**
     * Add a peer to the database
     * @param serialNumber serial number of the peer
     * @return true if was added, false otherwise
     */
    public static boolean addPeer(final UUID serialNumber) {
        final String query = "INSERT INTO peers VALUES (?);";
        final Object[] params = new Object[]{serialNumber.toString()};

        return executeUpdate(query, params);
    }

    /**
     * Remove a peer from the database
     * @param serialNumber serial number of the peer to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removePeer(final UUID serialNumber) {
        final String query = "DELETE FROM peers WHERE serial_number = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};

        return executeUpdate(query, params);
    }

    /*
        FILES TABLE
     */

    /**
     * Check if a given file from a peer is saved in the system
     * @param serialNumber serial number of the peer
     * @param path path of the file
     * @return true if is saved, false otherwise
     */
    public static boolean hasFile(final UUID serialNumber, final String path) {
        final String query = "SELECT uuid FROM files WHERE peer = ? AND name = ?;";
        final Object[] params = new Object[]{serialNumber.toString(), path};

        return hasResult(query, params);
    }

    /**
     * Add a file to the database
     * @param file file id to be added
     * @param serialNumber serial number of the peer that has the file
     * @param path path of the file
     * @return true if was added, false otherwise
     */
    public static boolean addFile(final UUID file, final UUID serialNumber, final String path) {
        final String query = "INSERT INTO files VALUES (?, ?, ?);";
        final Object[] params = new Object[]{file.toString(), serialNumber.toString(), path};

        return executeUpdate(query, params);
    }

    /**
     * Remove a file from the database
     * @param file file id to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removeFile(final UUID file) {
        final String query = "DELETE FROM files WHERE uuid = ?;";
        final Object[] params = new Object[]{file.toString()};

        return executeUpdate(query, params);
    }

    /**
     * Remove a file from the database
     * @param serialNumber serial number of the peer that own the file
     * @param path path of the file
     * @return true if was removed, false otherwise
     */
    public static boolean removeFile(final UUID serialNumber, final String path) {
        final String query = "DELETE FROM files WHERE peer = ? AND name = ?;";
        final Object[] params = new Object[]{serialNumber.toString(), path};

        return executeUpdate(query, params);
    }

    /**
     * Get the file id given its owner and path
     * @param serialNumber serial number of the peer
     * @param path path of the file
     * @return uuid of the file
     */
    public static UUID getFileId(final UUID serialNumber, final String path) {
        final String query = "SELECT uuid FROM files WHERE peer = ? AND name = ?;";
        final Object[] params = new Object[]{serialNumber.toString(), path};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return null;

        try {
            if(!result.next())
                return null;
            return UUID.fromString(result.getString("uuid"));
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the file id. " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a list with the IDs of all the chunks of a file
     * @param serialNumber serial number of the peer that owns the file
     * @param path path of the file
     * @return list with all the chunk ids of that file
     */
    public static List<Integer> getChunks(final UUID serialNumber, final String path) {
        final UUID uuid = getFileId(serialNumber, path);

        return getChunks(uuid);
    }

    /**
     * Get all the file ids of a peer
     * @param serialNumber serial number of the peer
     * @return list with all the file ids of a peer
     */
    public static List<UUID> getPeerFiles(final UUID serialNumber) {
        final String query = "SELECT uuid FROM files WHERE peer = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return null;

        try {
            final List<UUID> files = new ArrayList<>();
            while(result.next())
                files.add(UUID.fromString(result.getString("uuid")));

            return files;
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the files of a peer. " + e.getMessage());
            return null;
        }
    }

    /*
        CHUNKS TABLE
     */

    /**
     * Check if a given chunk from a file is saved in the system
     * @param file id of the file
     * @param chunkNumber number of the chunk
     * @return true if is saved, false otherwise
     */
    public static boolean hasChunk(final UUID file, final int chunkNumber) {
        final String query = "SELECT id FROM chunks WHERE file = ? AND chunk = ?;";
        final Object[] params = new Object[]{file.toString(), chunkNumber};

        return hasResult(query, params);
    }

    /**
     * Add a chunk of a file to the database
     * @param file file id of the chunk to be added
     * @param chunkNumber number of the chunk to add
     * @param minReplicas minimum replicas of the chunk
     * @return true if was added, false otherwise
     */
    public static boolean addChunk(final UUID file, final int chunkNumber, final int minReplicas) {
        final String query = "INSERT INTO chunks VALUES (?, ?, ?);";
        final Object[] params = new Object[]{file.toString(), chunkNumber, minReplicas};

        return executeUpdate(query, params);
    }

    /**
     * Remove a chunk of a file from the database
     * @param id id of the file chunk to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removeChunk(final int id) {
        final String query = "DELETE FROM chunks WHERE id = ?;";
        final Object[] params = new Object[]{id};

        return executeUpdate(query, params);
    }

    /**
     * Remove a chunk of a file from the database
     * @param file file id of the chunk to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removeChunk(final UUID file, final int chunkNumber) {
        final String query = "DELETE FROM chunks WHERE file = ? AND chunk = ?;";
        final Object[] params = new Object[]{file.toString(), chunkNumber};

        return executeUpdate(query, params);
    }

    /**
     * Get the chunk id of a given file chunk
     * @param file file to get the chunk id
     * @param chunkNumber chunk number to get the id
     * @return id of the chunk of that file or -1 in case of an error
     */
    public static int getChunkId(final UUID file, final int chunkNumber) {
        final String query = "SELECT id FROM chunks WHERE file = ? AND chunk = ?;";
        final Object[] params = new Object[]{file.toString(), chunkNumber};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return -1;

        try {
            if(!result.next())
                return -1;
            return result.getInt("id");
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the chunk id. " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get the chunk id of a given file chunk
     * @param file file to get the chunk id
     * @param chunkNumber chunk number to get the id
     * @return id of the chunk of that file or -1 in case of an error
     */
    public static int getMinReplicas(final UUID file, final int chunkNumber) {
        final String query = "SELECT minReplicas FROM chunks WHERE file = ? AND chunk = ?;";
        final Object[] params = new Object[]{file.toString(), chunkNumber};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return -1;

        try {
            if(!result.next())
                return -1;
            return result.getInt("minReplicas");
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the minimum replicas of a chunk. " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get a list with the IDs of all the chunks of a file
     * @param file id of the file to get the chunks
     * @return list with all the chunk ids of that file
     */
    public static List<Integer> getChunks(final UUID file) {
        final String query = "SELECT id FROM chunks WHERE file = ?;";
        final Object[] params = new Object[]{file.toString()};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return null;

        try {
            final List<Integer> chunks = new ArrayList<>();
            while(result.next())
                chunks.add(result.getInt("id"));

            return chunks;
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the chunks of a file. " + e.getMessage());
            return null;
        }
    }

    /*
        REPLICAS TABLE
     */

    /**
     * Check if a peer has a replica of a given chunk
     * @param serialNumber serial number of that peer
     * @param chunkId id of the file chunk to check
     * @return true if has, false otherwise
     */
    public static boolean hasChunkReplica(final UUID serialNumber, final int chunkId) {
        final String query = "SELECT peer FROM replicas WHERE peer = ? AND file_chunk = ?;";
        final Object[] params = new Object[]{serialNumber.toString(), chunkId};

        return hasResult(query, params);
    }

    /**
     * Add a chunk replica to the database
     * @param serialNumber serial number of the peer
     * @param chunkId id of the file chunk to add
     * @return true if was added, false otherwise
     */
    public static boolean addChunkReplica(final UUID serialNumber, final int chunkId) {
        final String query = "INSERT INTO replicas VALUES (?, ?);";
        final Object[] params = new Object[]{serialNumber.toString(), chunkId};

        return executeUpdate(query, params);
    }

    /**
     * Remove a chunk replica from the database
     * @param serialNumber serial number of the peer to be removed
     * @param chunkId id of the file chunk to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removeChunkReplica(final UUID serialNumber, final int chunkId) {
        final String query = "DELETE FROM replicas WHERE peer = ? AND file_chunk = ?;";
        final Object[] params = new Object[]{serialNumber.toString(), chunkId};

        return executeUpdate(query, params);
    }

    /**
     * Get the replication degree of a given chunk
     * @param chunkId id of the file chunk to get the replication degree
     * @return number of replicas of the chunk or -1 in case of error
     */
    public static int getChunkReplicationDegree(final int chunkId) {
        final String query = "SELECT COUNT(file_chunk) AS replicationDegree FROM replicas WHERE file_chunk = ?;";
        final Object[] params = new Object[]{chunkId};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return -1;

        try {
            if(!result.next())
                return -1;
            return result.getInt("replicationDegree");
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the replication degree of a chunk. " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get the peers that has replicated a given chunk
     * @param chunkId id of the file chunk to get the peers
     * @return peers with that chunk or null in case of error
     */
    public static List<UUID> getChunkPeers(final int chunkId) {
        final String query = "SELECT peer FROM replicas WHERE file_chunk = ?;";
        final Object[] params = new Object[]{chunkId};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return null;

        try {
            final List<UUID> peers = new ArrayList<>();
            while(result.next())
                peers.add(UUID.fromString(result.getString("peer")));

            return peers;
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the peers that has the chunk. " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the replicas of a given peer
     * @param serialNumber serial number of the peer
     * @return list with all replicas of the peer
     */
    public List<Integer> getPeerReplicas(final UUID serialNumber) {
        final String query = "SELECT file_chunk FROM replicas WHERE peer = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};

        final ResultSet result = executeQuery(query, params);
        if(result == null)
            return null;

        try {
            final List<Integer> chunks = new ArrayList<>();
            while(result.next())
                chunks.add(result.getInt("file_chunk"));

            return chunks;
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could get the replicas of a peer. " + e.getMessage());
            return null;
        }
    }

    /*
        UTILITIES
     */

    /**
     * Check if something exists in the database
     * @param query query statement to execute
     * @param params params of the query
     * @return true if it has result, false otherwise
     */
    private static boolean hasResult(final String query, final Object[] params) {
        final ResultSet result  = executeQuery(query, params);
        if(result == null)
            return false;

        try {
            return result.next();
        } catch (SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could not check if has result. " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if something exists in the database
     * @param query query statement to execute
     * @param params params of the query
     * @return true if it has result, false otherwise
     */
    private static ResultSet executeQuery(final String query, final Object[] params) {
        final Database database = Relay.getInstance().getDatabase();
        if(database == null)
            return null;

        try {
            return database.executeQuery(query, params);
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Query could not be executed. " + e.getMessage());
            return null;
        }
    }

    /**
     * Execute a update in the database
     * @param query query to be executed
     * @param params params of the query
     * @return true if something was updated, false otherwise
     */
    private static boolean executeUpdate(final String query, final Object[] params) {
        final Database database = Relay.getInstance().getDatabase();
        if(database == null)
            return false;

        try {
            return database.executeUpdate(query, params) > 0;
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Update could not be executed. " + e.getMessage());
            return false;
        }
    }
}