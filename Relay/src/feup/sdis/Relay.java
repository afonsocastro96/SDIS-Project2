package feup.sdis;
import feup.sdis.database.Database;
import feup.sdis.database.types.DatabaseType;
import feup.sdis.logger.Level;
import feup.sdis.network.SSLServer;


import java.io.*;
import java.util.Properties;
/**
 * Relay of the Distributed Backup Service Over The Internet
 */
public class Relay extends Node {

    /**
     * Instance of the relay server
     */
    private static Relay instance;

    /**
     * SSL server
     */
    private SSLServer server;

    /**
     * Database of the relay server
     */
    private Database database;

    /**
     * Main method of the program
     *
     * @param args arguments sent to the console
     */
    public static void main(String[] args) {
        instance = new Relay();

        // Starting the peer
        getLogger().log(Level.INFO, "Starting the service.");

        if(!getInstance().createConfig())
            return;
        if(!getInstance().loadConfig())
            return;
        if(!getInstance().getDatabase().connect())
            return;
        if(!getInstance().getServer().start())
            return;

        // Start the server
        getLogger().log(Level.INFO, "Service started.");
    }

    /**
     * Get the instance of the relay
     *
     * @return instance of the relay
     */
    public static Relay getInstance() {
        return instance;
    }

    /**
     * Constructor of Relay
     */
    private Relay() {
        super("Relay");

        // Environment variables for SSL
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE);
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    }

    /**
     * Get the database of the server
     * @return database of the server
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Get the server
     * @return server
     */
    public SSLServer getServer() {
        return server;
    }

    /**
     * Create the configuration file
     *
     * @return true if successful, false otherwise
     */
    @Override
    boolean createConfig() {
        File configFile = new File(CONFIG_FILE);
        if(configFile.exists()) return true;

        Properties properties = new Properties();
        OutputStream output = null;

        try {
            if(!configFile.createNewFile()) {
                getLogger().log(Level.FATAL, "Could not create the configuration file.");
                return false;
            }

            output = new FileOutputStream(configFile);

            // Set the properties values
            properties.setProperty("log", "info");
            properties.setProperty("host", "127.0.0.1");
            properties.setProperty("port", "21852");
            properties.setProperty("dbtype", "mysql");
            properties.setProperty("dbhost", "localhost");
            properties.setProperty("dbport", "3306");
            properties.setProperty("dbname", "database");
            properties.setProperty("dbschema", "schema");
            properties.setProperty("dbuser", "username");
            properties.setProperty("dbpassword", "password");

            // Save the file
            properties.store(output, null);

            getLogger().log(Level.INFO, "Configuration file has been created.");
            return true;
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not create the configuration file. " + e.getMessage());
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    getLogger().log(Level.FATAL, "Could not close the configuration file. " + e.getMessage());
                }
            }
        }
    }

    /**
     * Load the configuration file
     *
     * @return true if successful, false otherwise
     */
    @Override
    boolean loadConfig() {
        Properties properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE);

            // Load the properties file
            properties.load(input);

            // Logger
            String logLevel = properties.getProperty("log");
            try {
                getLogger().setLevel(Level.valueOf(logLevel.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                getLogger().log(Level.WARNING, "Invalid value for log property. Using default " + getLogger().getLevel());
            }
            getLogger().log(Level.DEBUG, "Log level - " + logLevel);

            // Server socket
            String host = properties.getProperty("host");
            getLogger().log(Level.DEBUG, "Server host - " + host);

            int port;
            try {
                port = Integer.parseInt(properties.getProperty("port"));
                getLogger().log(Level.DEBUG, "Server port - " + port);
            } catch (NumberFormatException ignored) {
                getLogger().log(Level.FATAL, "Invalid value for server port property.");
                return false;
            }

            // Database
            String dbType = properties.getProperty("dbtype"),
                    dbHost = properties.getProperty("dbhost"),
                    dbName = properties.getProperty("dbname"),
                    dbSchema = properties.getProperty("dbschema"),
                    dbUser = properties.getProperty("dbuser"),
                    dbPassword = properties.getProperty("dbpassword");
            getLogger().log(Level.DEBUG, "Database type - " + dbType);
            getLogger().log(Level.DEBUG, "Database host - " + dbHost);
            getLogger().log(Level.DEBUG, "Database name - " + dbName);
            getLogger().log(Level.DEBUG, "Database schema - " + dbSchema);
            getLogger().log(Level.DEBUG, "Database user - " + dbUser);
            getLogger().log(Level.DEBUG, "Database password - " + dbPassword);

            int dbPort;
            try {
                dbPort = Integer.parseInt(properties.getProperty("dbport"));
                getLogger().log(Level.DEBUG, "Database port - " + dbPort);
            } catch (NumberFormatException ignored) {
                getLogger().log(Level.FATAL, "Invalid value for database port property.");
                return false;
            }
            server = new SSLServer(host, port);

            DatabaseType databaseType;
            try {
                databaseType = DatabaseType.valueOf(dbType.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                getLogger().log(Level.FATAL, "Invalid value for database type property.");
                return false;
            }
            database = Database.createDatabase(databaseType, dbHost, dbPort, dbName, dbSchema, dbUser, dbPassword);

            getLogger().log(Level.INFO, "Configuration has been loaded.");
            return true;
        } catch (IOException e) {
            getLogger().log(Level.FATAL, "Could not load the configuration file. " + e.getMessage());
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    getLogger().log(Level.FATAL, "Could not close the configuration file. " + e.getMessage());
                }
            }
        }
    }
}