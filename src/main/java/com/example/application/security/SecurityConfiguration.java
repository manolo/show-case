package com.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

@Configuration
@EnableWebSecurity @Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   @Bean
   public SecurityFilterChain vaadinSecurityFilterChain(HttpSecurity http) throws Exception {
       http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/images/*.png").permitAll());
       http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/line-awesome/**").permitAll());

       // Allow REST API without login (optional if there are spring webservices exposed)
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/**").permitAll());

       http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> {
           vaadin.loginView(LoginView.class);
       });
       return http.build();
   }
}
