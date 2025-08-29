package adapter;

import co.com.pragma.model.common.gateways.LoggingGateway;
import org.springframework.stereotype.Component;
import util.LoggingUtil;

@Component
public class LoggingAdapter implements LoggingGateway {

    @Override
    public void info(String action, Object details) {
        LoggingUtil.info(action, details);
    }

    @Override
    public void warn(String action, Object details, Throwable ex) {
        LoggingUtil.warn(action, details, ex);
    }

    @Override
    public void error(String action, Object details, Throwable ex) {
        LoggingUtil.error(action, details, ex);
    }

    @Override
    public void debug(String action, Object details) {
        LoggingUtil.debug(action, details);
    }
}