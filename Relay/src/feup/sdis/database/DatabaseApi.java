package feup.sdis.database;

import feup.sdis.Relay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Database queries
 */
public class DatabaseApi {

    /**
     * Check if the system has already acknowledge of a given peer
     * @param serialNumber serial number of that peer
     * @return true if has, false otherwise
     */
    public static boolean hasPeer(final UUID serialNumber) {
        final Database database = Relay.getInstance().getDatabase();
        if(database == null)
            return false;

        final String query = "SELECT serial_number FROM peers WHERE serial_number = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};
        ResultSet resultSet = null;
        try {
            resultSet = database.executeQuery(query, params);
            return resultSet.isBeforeFirst();
        } catch (SQLException ignored) {
            return false;
        } finally {
            if(resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
        }
    }

    /**
     * Add a peer to the database
     * @param serialNumber serial number of the peer
     * @return true if was added, false otherwise
     */
    public static boolean addPeer(final UUID serialNumber) {
        final Database database = Relay.getInstance().getDatabase();
        if(database == null)
            return false;

        final String query = "INSERT INTO peers VALUES (?);";
        final Object[] params = new Object[]{serialNumber.toString()};
        return database.executeUpdate(query, params) > 0;
    }

    /**
     * Remove a peer from the database
     * @param serialNumber serial number of the peer to be removed
     * @return true if was removed, false otherwise
     */
    public static boolean removePeer(final UUID serialNumber) {
        final Database database = Relay.getInstance().getDatabase();
        if(database == null)
            return false;

        final String query = "DELETE FROM peers WHERE serial_number = ?;";
        final Object[] params = new Object[]{serialNumber.toString()};
        return database.executeUpdate(query, params) > 0;
    }
}
