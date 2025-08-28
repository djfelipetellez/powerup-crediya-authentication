package co.com.pragma.model.common;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingUtil {

    private static final Logger logger = Logger.getLogger(LoggingUtil.class.getName());

    private LoggingUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void info(String action, Object details) {
        logger.info(String.format("[ACTION: %s] - %s", action, details));
    }

    public static void warn(String action, Object details, Throwable ex) {
        String message = String.format("[ACTION: %s] - %s - Exception: %s",
                action, details, ex != null ? ex.getMessage() : "null");
        if (ex != null) {
            logger.log(Level.WARNING, message, ex);
        } else {
            logger.warning(message);
        }
    }

    public static void error(String action, Object details, Throwable ex) {
        String message = String.format("[ACTION: %s] - %s", action, details);
        if (ex != null) {
            logger.log(Level.SEVERE, message, ex);
        } else {
            logger.severe(message);
        }
    }

    public static void debug(String action, Object details) {
        logger.fine(String.format("[ACTION: %s] - %s", action, details));
    }
}
