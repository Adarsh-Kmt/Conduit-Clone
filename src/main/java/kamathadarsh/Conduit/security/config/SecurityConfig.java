package kamathadarsh.Conduit.security.config;

import kamathadarsh.Conduit.security.filter.JWTAuthFilter;
import kamathadarsh.Conduit.security.securityExceptionHandler.JWTAuthEntryPoint;
import kamathadarsh.Conduit.security.service.CustomUserDetailsService;
import kamathadarsh.Conduit.security.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity(debug = true)
@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JWTAuthEntryPoint jwtAuthEntryPoint;

    private final JWTUtils jwtUtils;

    private final CustomUserDetailsService customUserDetailsService;

    public JWTAuthFilter jwtAuthFilter(){

        return new JWTAuthFilter(jwtUtils, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){

        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailsService);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .exceptionHandling(exceptionHandler -> exceptionHandler.authenticationEntryPoint(jwtAuthEntryPoint))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
