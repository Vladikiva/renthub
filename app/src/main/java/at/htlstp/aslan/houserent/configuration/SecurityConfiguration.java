package at.htlstp.aslan.houserent.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Users are provided through configuration ({@code renthub.security.*}) so that
     * credentials come from the environment (secrets, not source code).
     */
    @Bean
    public UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(properties.users().stream()
                .map(user -> User.withUsername(user.username())
                        .password(passwordEncoder.encode(user.password()))
                        .roles(user.role())
                        .build())
                .toList());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomSuccessHandler successHandler) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus")
                        .permitAll()
                        .requestMatchers("/actuator/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/employee/**")
                        .hasRole("EMPLOYEE")
                        .anyRequest()
                        .permitAll())
                .httpBasic(basic -> {})
                .formLogin(form -> form.successHandler(successHandler))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(handling -> handling.accessDeniedPage("/login"))
                // The REST API is stateless and token-less; the HTML views keep CSRF protection.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/admin/**"))
                .build();
    }
}
