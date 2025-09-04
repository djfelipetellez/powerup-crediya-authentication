package co.com.pragma.model.common.gateways;

public interface LogGateway {
    void info(String action, Object details);

    void warn(String action, Object details, Throwable ex);

    void error(String action, Object details, Throwable ex);

    void debug(String action, Object details);
}