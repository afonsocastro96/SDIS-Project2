package feup.sdis.logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger Class
 */
public class Logger {

    /**
     * Name of the logger
     */
    private final String name;

    /**
     * Minimum level of the messages to be logged
     */
    private final Level minLevel;

    /**
     * File writer
     */
    private final PrintWriter writer;

    /**
     * Date formate of the log
     */
    private final DateFormat dateFormat;

    /**
     * Constructor of the Logger
     *
     * @param name     name of the logger
     * @param minLevel minimum level to log the message
     * @throws FileNotFoundException        if the file is not writable or is a directory
     * @throws UnsupportedEncodingException if the encoding does not exist
     */
    public Logger(final String name, final Level minLevel) throws FileNotFoundException, UnsupportedEncodingException {
        this.name = name + ".log";
        this.minLevel = minLevel;
        this.writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(name, true), "UTF-8"));
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    /**
     * Log a message to the logger file
     *
     * @param level   level of the message
     * @param message message to be logged
     */
    public void log(final Level level, final String message) {
        if(!level.isWorseOrEqual(minLevel))
            return;

        String logRecord = String.format("%s [%s] %s", dateFormat.format(new Date()), level.toString(), message);

        writer.write(logRecord);
        writer.flush();
        System.out.print(logRecord);
    }
}
