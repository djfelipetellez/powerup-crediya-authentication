package co.com.pragma.logutil.adapter;

import co.com.pragma.logutil.LogUtil;
import co.com.pragma.model.common.gateways.LogGateway;
import org.springframework.stereotype.Component;

@Component
public class LogAdapter implements LogGateway {

    @Override
    public void info(String action, Object details) {
        LogUtil.info(action, details);
    }

    @Override
    public void warn(String action, Object details, Throwable ex) {
        LogUtil.warn(action, details, ex);
    }

    @Override
    public void error(String action, Object details, Throwable ex) {
        LogUtil.error(action, details, ex);
    }

    @Override
    public void debug(String action, Object details) {
        LogUtil.debug(action, details);
    }
}