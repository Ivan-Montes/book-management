package dev.ime.common.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Profile("!prod")
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    	
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/**").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**","/webjars/swagger-ui/**").permitAll()
                .pathMatchers("/actuator/metrics").authenticated()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .csrf(CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .build();        
    }

    @Bean
    @Profile("prod")
    SecurityWebFilterChain productionSecurityFilterChain(ServerHttpSecurity http) {
    	
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/**").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/swagger-ui/**").permitAll()
                .pathMatchers("/actuator/metrics").authenticated()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .csrf(CsrfSpec::disable)
            .build();        
    }
}
