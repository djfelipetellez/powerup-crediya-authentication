package co.com.pragma.api.exception;

import co.com.pragma.model.common.gateways.LogGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private LogGateway logGateway;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ServerCodecConfigurer serverCodecConfigurer;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(serverCodecConfigurer.getWriters()).thenReturn(Collections.emptyList());
        when(applicationContext.getClassLoader()).thenReturn(GlobalExceptionHandlerTest.class.getClassLoader());
        globalExceptionHandler = new GlobalExceptionHandler(
                new DefaultErrorAttributes(),
                applicationContext,
                serverCodecConfigurer,
                logGateway
        );
    }

    @Test
    void shouldCreateGlobalExceptionHandlerSuccessfully() {
        // Given & When - Already created in setUp()

        // Then
        assertThat(globalExceptionHandler).isNotNull();
        verify(serverCodecConfigurer).getWriters();
    }
}