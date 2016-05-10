package feup.sdis.database.types;

/**
 * Types of Databases
 */
public enum DatabaseType {
    /**
     * MySQL
     */
    MYSQL,

    /**
     * Oracle
     */
    ORACLE,

    /**
     * Postgres
     */
    POSTGRES;

    /**
     * Get the database type by its name
     * @param name name of the type
     * @return database type
     */
    public static DatabaseType getByName(final String name) {
        for(final DatabaseType type : values())
            if(type.toString().equalsIgnoreCase(name))
                return type;
        return null;
    }
}