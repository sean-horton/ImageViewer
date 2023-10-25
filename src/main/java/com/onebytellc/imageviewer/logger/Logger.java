package com.onebytellc.imageviewer.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * I could use log4j, but I wanted to write my own implementation because it's fun and
 * this is a fun project.
 */
public class Logger {

    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();
    private static final LogWriter LOG_WRITER = new LogWriter();
    private static volatile LogLevel LEVEL = LogLevel.INFO;

    private String name;

    private Logger(String name) {
        this.name = name;
    }


    /////////////////////
    // PUBLIC STATIC
    public static synchronized void setLevel(LogLevel level) {
        LEVEL = level;
    }

    public static synchronized <T> Logger getInstance(Class<T> clazz) {
        return LOGGERS.computeIfAbsent(clazz.getName(), k -> new Logger(clazz.getName()));
    }

    public static synchronized void stopAndWait() {
        LOG_WRITER.stop();
    }

    public static synchronized void addLogAppender(LogAppender logAppender) {
        LOG_WRITER.addLogOutput(logAppender);
    }


    /////////////////////
    // PUBLIC
    public void error(String string, Object... values) {
        if (LEVEL.ordinal() < LogLevel.ERROR.ordinal()) {
            return;
        }
        LOG_WRITER.offer(new LogItem(name, string, values));
    }

    public void warn(String string, Object... values) {
        if (LEVEL.ordinal() < LogLevel.WARN.ordinal()) {
            return;
        }
        LOG_WRITER.offer(new LogItem(name, string, values));
    }

    public void info(String string, Object... values) {
        if (LEVEL.ordinal() < LogLevel.INFO.ordinal()) {
            return;
        }
        LOG_WRITER.offer(new LogItem(name, string, values));
    }

    public void debug(String string, Object... values) {
        if (LEVEL.ordinal() < LogLevel.DEBUG.ordinal()) {
            return;
        }
        LOG_WRITER.offer(new LogItem(name, string, values));
    }

}
