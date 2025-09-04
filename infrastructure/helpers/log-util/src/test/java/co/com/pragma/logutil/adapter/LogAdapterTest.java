package co.com.pragma.logutil.adapter;

import co.com.pragma.logutil.LogUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class LogAdapterTest {

    private LogAdapter logAdapter;

    @BeforeEach
    void setUp() {
        logAdapter = new LogAdapter();
    }

    @Test
    void shouldImplementLogGateway() {
        // Then
        assertNotNull(logAdapter);
        assertTrue(true);
    }

    @Test
    void shouldCallLogUtilInfo() {
        // Given
        String action = "TestAction";
        String details = "Test details";

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.info(action, details);

            // Then
            mockedLogUtil.verify(() -> LogUtil.info(action, details));
        }
    }

    @Test
    void shouldCallLogUtilWarn() {
        // Given
        String action = "TestAction";
        String details = "Test warning";
        Exception exception = new RuntimeException("Test exception");

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.warn(action, details, exception);

            // Then
            mockedLogUtil.verify(() -> LogUtil.warn(action, details, exception));
        }
    }

    @Test
    void shouldCallLogUtilError() {
        // Given
        String action = "TestAction";
        String details = "Test error";
        Exception exception = new RuntimeException("Test exception");

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.error(action, details, exception);

            // Then
            mockedLogUtil.verify(() -> LogUtil.error(action, details, exception));
        }
    }

    @Test
    void shouldCallLogUtilDebug() {
        // Given
        String action = "TestAction";
        String details = "Test debug";

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.debug(action, details);

            // Then
            mockedLogUtil.verify(() -> LogUtil.debug(action, details));
        }
    }

    @Test
    void shouldHandleNullAction() {
        // Given
        String details = "Test details";

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.info(null, details);

            // Then
            mockedLogUtil.verify(() -> LogUtil.info(null, details));
        }
    }

    @Test
    void shouldHandleNullDetails() {
        // Given
        String action = "TestAction";

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.info(action, null);

            // Then
            mockedLogUtil.verify(() -> LogUtil.info(action, null));
        }
    }

    @Test
    void shouldHandleNullException() {
        // Given
        String action = "TestAction";
        String details = "Test details";

        try (MockedStatic<LogUtil> mockedLogUtil = mockStatic(LogUtil.class)) {
            // When
            logAdapter.error(action, details, null);

            // Then
            mockedLogUtil.verify(() -> LogUtil.error(action, details, null));
        }
    }
}