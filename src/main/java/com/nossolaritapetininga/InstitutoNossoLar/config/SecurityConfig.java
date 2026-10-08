package com.nossolaritapetininga.InstitutoNossoLar.config;

import com.nossolaritapetininga.InstitutoNossoLar.service.CustomUserDetailsService;
import com.nossolaritapetininga.InstitutoNossoLar.security.AlteracaoSenhaObrigatoriaFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AlteracaoSenhaObrigatoriaFilter alteracaoSenhaObrigatoriaFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            AlteracaoSenhaObrigatoriaFilter alteracaoSenhaObrigatoriaFilter) {
        this.userDetailsService = userDetailsService;
        this.alteracaoSenhaObrigatoriaFilter = alteracaoSenhaObrigatoriaFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/",
                    "/login",
                    "/recuperar-senha",
                    "/redefinir-senha",
                    "/acesso-negado",
                    "/css/**",
                    "/js/**",
                    "/NL_Img/**",
                    "/midias/**",
                    "/img/**",
                    "/error"
                ).permitAll()

                .requestMatchers("/alterar-senha")
                .authenticated()

                .requestMatchers("/admin/**")
                .authenticated()

                .anyRequest()
                .denyAll()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl(
                    "/admin/dashboard",
                    true
                )
                .failureUrl("/login?erro")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )

            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acesso-negado")
            )

            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(1)
                .expiredUrl("/login?expirado")
                .sessionRegistry(sessionRegistry())
            )

            .addFilterAfter(
                alteracaoSenhaObrigatoriaFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
