package kz.iitu.spring_lab_01.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import kz.iitu.spring_lab_01.service.LoginAttemptService;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginAttemptService loginAttemptService;

    public SecurityConfig(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**").permitAll()
                        .requestMatchers("/api/lab4/**").permitAll()
                        .requestMatchers("/api/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/books/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(new SimpleUrlAuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                                Authentication authentication) throws IOException, ServletException {
                                loginAttemptService.loginSucceeded(authentication.getName());
                                request.getSession().removeAttribute("LOGIN_ERROR");
                                super.onAuthenticationSuccess(request, response, authentication);
                            }
                        })
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error") {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                                AuthenticationException exception) throws IOException, ServletException {
                                String username = request.getParameter("username");
                                String errorMessage;

                                if (exception instanceof LockedException || loginAttemptService.isBlocked(username)) {
                                    long sec = loginAttemptService.getSecondsLeft(username);
                                    errorMessage = "Account is locked! Try again in " + sec + " seconds.";
                                } else {
                                    loginAttemptService.loginFailed(username);
                                    if (loginAttemptService.isBlocked(username)) {
                                        long sec = loginAttemptService.getSecondsLeft(username);
                                        errorMessage = "Too many failed attempts! Account locked for " + sec + " seconds.";
                                    } else {
                                        int left = loginAttemptService.getRemainingAttempts(username);
                                        errorMessage = "Invalid credentials. Attempts left: " + left;
                                    }
                                }

                                request.getSession().setAttribute("LOGIN_ERROR", errorMessage);
                                super.onAuthenticationFailure(request, response, exception);
                            }
                        })
                )
                .logout(logout -> logout.logoutSuccessUrl("/?logout"));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}