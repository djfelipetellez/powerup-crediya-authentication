package co.com.pragma.security.config;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.security.jwt.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static co.com.pragma.security.config.SecurityConstants.*;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final ServerSecurityContextRepository securityContextRepository;
    private final LogGateway logGateway;

    public SecurityConfig(ServerSecurityContextRepository securityContextRepository, LogGateway logGateway) {
        this.securityContextRepository = securityContextRepository;
        this.logGateway = logGateway;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            String path = exchange.getRequest().getURI().getPath();
            logGateway.warn(LOG_AUTHENTICATION_ENTRY_POINT,
                    String.format(MSG_AUTH_ERROR_FOR_PATH, path, ex.getMessage()), ex);

            ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

            // Identificar si es token expirado
            if ((ex.getCause() instanceof org.springframework.security.authentication.BadCredentialsException badCreds &&
                    badCreds.getMessage() != null && badCreds.getMessage().contains("Token expirado")) ||
                    (ex.getMessage() != null && ex.getMessage().contains("Token expirado"))) {
                problemDetail.setType(URI.create(PROBLEM_TYPE_TOKEN_EXPIRED));
                problemDetail.setTitle(TITLE_TOKEN_EXPIRED);
                problemDetail.setDetail(DETAIL_TOKEN_EXPIRED);
            } else {
                problemDetail.setType(URI.create(PROBLEM_TYPE_AUTHENTICATION));
                problemDetail.setTitle(TITLE_AUTHENTICATION);
                problemDetail.setDetail(DETAIL_AUTHENTICATION);
            }

            problemDetail.setInstance(exchange.getRequest().getURI());

            try {
                ObjectMapper mapper = new ObjectMapper();
                byte[] bytes = mapper.writeValueAsBytes(problemDetail);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);

                return exchange.getResponse().writeWith(Mono.just(buffer));
            } catch (Exception e) {
                return Mono.error(e);
            }
        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            String path = exchange.getRequest().getURI().getPath();
            logGateway.warn(LOG_ACCESS_DENIED_HANDLER,
                    String.format(MSG_ACCESS_DENIED_FOR_PATH, path, denied.getMessage()), denied);

            ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
            problemDetail.setType(URI.create(PROBLEM_TYPE_ACCESS_DENIED));
            problemDetail.setTitle(TITLE_ACCESS_DENIED);
            problemDetail.setDetail(DETAIL_ACCESS_DENIED);
            problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_ACCESS_DENIED);
            problemDetail.setProperty(PROPERTY_SUGGESTION, SUGGESTION_CONTACT_ADMIN);
            problemDetail.setInstance(exchange.getRequest().getURI());

            try {
                ObjectMapper mapper = new ObjectMapper();
                byte[] bytes = mapper.writeValueAsBytes(problemDetail);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);

                return exchange.getResponse().writeWith(Mono.just(buffer));
            } catch (Exception e) {
                return Mono.error(e);
            }
        };
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter,
                                              ServerAccessDeniedHandler accessDeniedHandler,
                                              ServerAuthenticationEntryPoint authenticationEntryPoint) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers("/api/v1/auth/login").permitAll()
                        .pathMatchers("/api/v1/auth/validate-token").permitAll()
                        .pathMatchers("/api/v1/usuarios/validar-existencia/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}