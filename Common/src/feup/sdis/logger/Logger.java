package feup.sdis.logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Logger Class
 */
public class Logger {

    /**
     * Log file
     */
    static final String LOG_FILE = "node.log";

    /**
     * Name of the logger
     */
    private final String name;

    /**
     * Minimum level of the messages to be logged
     */
    private Level minLevel;

    /**
     * File writer
     */
    private final PrintWriter writer;

    /**
     * Date format of the log
     */
    private final DateFormat dateFormat;

    /**
     * Constructor of the Logger
     *
     * @param name name of the logger
     * @throws FileNotFoundException        if the file is not writable or is a directory
     * @throws UnsupportedEncodingException if the encoding does not exist
     */
    public Logger(final String name) throws FileNotFoundException, UnsupportedEncodingException {
        this(name, Level.INFO);
    }

    /**
     * Constructor of the Logger
     *
     * @param name     name of the logger
     * @param minLevel minimum level to log the message
     * @throws FileNotFoundException        if the file is not writable or is a directory
     * @throws UnsupportedEncodingException if the encoding does not exist
     */
    public Logger(final String name, final Level minLevel) throws FileNotFoundException, UnsupportedEncodingException {
        this.name = name;
        this.minLevel = minLevel;
        this.writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE, true), "UTF-8"));
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Get the logger level
     * @return logger level
     */
    public Level getLevel() {
        return minLevel;
    }

    /**
     * Set the minimum level to log the messages
     * @param minLevel minimum level to log the messages
     */
    public void setLevel(final Level minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Log a message to the logger file
     *
     * @param level   level of the message
     * @param message message to be logged
     */
    public void log(final Level level, final String message) {
        if (!level.isWorseOrEqual(minLevel))
            return;

        String logRecord = String.format("%s [%s] %s\n", dateFormat.format(new Date()), level.toString(), message);

        writer.write(logRecord);
        writer.flush();
        System.out.print("[" + name + "] " + logRecord);
    }
}