package co.com.pragma.api.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUtil {

    private LoggingUtil() {
    }

    public static void info(String action, Object details) {
        log.info("[ACTION: {}] - {}", action, details);
    }

    public static void warn(String action, Object details, Throwable ex) {
        log.warn("[ACTION: {}] - {} - Exception: {}", action, details, ex.getMessage());
    }

    public static void error(String action, Object details, Throwable ex) {
        log.error("[ACTION: {}] - {} - Exception: ", action, details, ex);
    }

    public static void debug(String action, Object details) {
        log.debug("[ACTION: {}] - {}", action, details);
    }
}
