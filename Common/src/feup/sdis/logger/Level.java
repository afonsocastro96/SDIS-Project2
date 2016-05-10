package feup.sdis.logger;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * Logger level
 */
public enum Level {
    /**
     * Information only
     */
    INFO(1),

    /**
     * Debug messages
     */
    DEBUG(0),

    /**
     * Warning messages
     */
    WARNING(2),

    /**
     * Error messages
     */
    ERROR(3),

    /**
     * Fatal messages
     */
    FATAL(4);

    /**
     * Severity of the level
     */
    private int severity;

    /**
     * Constructor of Level
     * @param severity severity of the level
     */
    Level(final int severity) {
        this.severity = severity;
    }

    /**
     * Convert a level to a string
     * @return converted string
     */
    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString());
    }

    /**
     * Compare if a level is worse or equal to another
     * @param other level to be compared
     * @return true if this level is worse or equal
     */
    public boolean isWorseOrEqual(Level other) {
        return this.severity >= other.severity;
    }
}
