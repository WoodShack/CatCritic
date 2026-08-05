package com.cpan228.catcritic.config;

import com.cpan228.catcritic.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/about",
                        "/register",
                        "/login",
                        "/access-denied"
                ).permitAll()

                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/uploads/**",
                        "/webjars/**",
                        "/favicon.ico"
                ).permitAll()

                .requestMatchers("/h2-console/**").permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/cats",
                        "/cats/{id:[0-9]+}"
                ).permitAll()

                .requestMatchers(
                        "/cats/new",
                        "/cats/*/rate"
                ).authenticated()

                .requestMatchers(
                        "/cats/*/edit",
                        "/cats/*/delete"
                ).authenticated()

                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                .anyRequest()
                .authenticated()
            )

            .formLogin(login -> login
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/cats", false)
                    .failureUrl("/login?error")
                    .permitAll()
            )

            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/?logout")
                    .permitAll()
            )

            .exceptionHandling(exception -> exception
                    .accessDeniedPage("/access-denied")
            )

            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**")
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )

            .headers(headers -> headers
                    .frameOptions(
                            HeadersConfigurer.FrameOptionsConfig::sameOrigin
                    )
            );

        return http.build();
    }
}
