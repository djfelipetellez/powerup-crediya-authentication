package co.com.pragma.logutil;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public class LogUtil {
    private static final Logger logger = LogManager.getLogger(LogUtil.class);

    public static void info(String action, Object details) {
        logger.info("[ACTION: {}] - {}", action, details);
    }

    public static void warn(String action, Object details, Throwable ex) {
        if (ex != null) {
            logger.warn("[ACTION: {}] - {} - Exception: {}", action, details, ex.getMessage(), ex);
        } else {
            logger.warn("[ACTION: {}] - {} - Exception: null", action, details);
        }
    }

    public static void error(String action, Object details, Throwable ex) {
        if (ex != null) {
            logger.error("[ACTION: {}] - {}", action, details, ex);
        } else {
            logger.error("[ACTION: {}] - {}", action, details);
        }
    }

    public static void debug(String action, Object details) {
        logger.debug("[ACTION: {}] - {}", action, details);
    }
}