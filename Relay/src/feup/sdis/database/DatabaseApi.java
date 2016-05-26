package feup.sdis.database;

import feup.sdis.Node;
import feup.sdis.Relay;
import feup.sdis.logger.Level;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        final String query = "SELECT * FROM peers WHERE serial_number = ?;";
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
        final String query = "SELECT * FROM files WHERE peer = ? AND name = ?;";
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
        final String query = "SELECT * FROM files WHERE peer = ? AND name = ?;";
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
