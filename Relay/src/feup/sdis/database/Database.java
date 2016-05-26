package feup.sdis.database;

import feup.sdis.Node;
import feup.sdis.database.types.DatabaseType;
import feup.sdis.database.types.MySQL;
import feup.sdis.database.types.OracleSQL;
import feup.sdis.database.types.PostgreSQL;
import feup.sdis.logger.Level;

import java.sql.*;

/**
 * Database Object.
 */
public abstract class Database {

    /**
     * Database driver
     */
    private final String driver;

    /**
     * URL connection
     */
    private final String url;

    /**
     * Username of the Database
     */
    private final String username;

    /**
     * Password of the Database
     */
    private final String password;

    /**
     * Database connection
     */
    private Connection connection;

    /**
     * Constructor of Database
     *
     * @param driver   driver of the database
     * @param url      url for connecting to the database
     * @param username username of the database
     * @param password password of the database
     */
    protected Database(final String driver, final String url, final String username, final String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Create a database
     *
     * @param type     type of the database
     * @param hostname hostname of the database
     * @param port     port of the database
     * @param database database of the database
     * @param schema   schema of the database
     * @param username username of the database
     * @param password password of the database
     * @return created database
     */
    public static Database createDatabase(final DatabaseType type, final String hostname, final int port, final String database, final String schema, final String username, final String password) {
        switch (type) {
            case MYSQL:
                return new MySQL(hostname, port, database, username, password);
            case ORACLE:
                return new OracleSQL(hostname, port, database, username, password);
            case POSTGRES:
                return new PostgreSQL(hostname, port, database, schema, username, password);
            default:
                return null;
        }
    }

    /**
     * Connect to the database
     */
    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                Node.getLogger().log(Level.WARNING, "Connection already established with the database.");
                return false;
            }

            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);

            keepAlive();

            Node.getLogger().log(Level.INFO, "Established connection with the database.");
            return true;
        } catch (final SQLException | ClassNotFoundException e) {
            Node.getLogger().log(Level.FATAL, "Could not connect to the database. " + e.getMessage());
            return false;
        }
    }

    /**
     * Close the connection to the database
     */
    public boolean close() {
        try {
            if (connection != null)
                connection.close();

            Node.getLogger().log(Level.INFO, "Closed connection with the database.");
            return true;
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the connection to the database. " + e.getMessage());
            return false;
        }
    }

    /**
     * Check the connection to the database
     *
     * @return true if closed, false otherwise
     */
    public boolean isClosed() {
        try {
            if (connection != null)
                return connection.isClosed();
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could not check the connection with the database. " + e.getMessage());
        }
        return true;
    }

    /**
     * Close a statement
     *
     * @param statement statement to be closed
     */
    public void close(final Statement statement) {
        try {
            if (statement != null)
                statement.close();
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the statement. " + e.getMessage());
        }
    }

    /**
     * Close a ResultSet
     *
     * @param resultSet result set to be closed
     */
    public void close(final ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (final SQLException e) {
            Node.getLogger().log(Level.ERROR, "Could not close the result set. " + e.getMessage());
        }
    }

    /**
     * Query Database
     *
     * @param sql        sql query
     * @param parameters parameters of the prepared statement
     * @return result of the given query
     */
    public ResultSet executeQuery(final String sql, final Object[] parameters) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        int index = 0;
        for (final Object parameter : parameters)
            preparedStatement.setObject(++index, parameter);

        return preparedStatement.executeQuery();
    }

    /**
     * Update Database
     *
     * @param sql        sql statement
     * @param parameters parameters of the prepared statement
     * @return number of rows updated
     */
    public int executeUpdate(final String sql, final Object[] parameters) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        int index = 0;
        for (final Object parameter : parameters)
            preparedStatement.setObject(++index, parameter);

        int numberRowsUpdated = preparedStatement.executeUpdate();
        close(preparedStatement);

        return numberRowsUpdated;
    }

    /**
     * Keep the connection alive
     */
    public void keepAlive() {
        new Thread(() -> {
            while (!isClosed()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

                try {
                    executeQuery("SELECT 1;", new Object[0]);
                } catch (SQLException ignored) {
                }
            }
            connect();
        }).start();
    }
}