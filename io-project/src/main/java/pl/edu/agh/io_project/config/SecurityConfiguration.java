package pl.edu.agh.io_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/**").permitAll()
                        // .requestMatchers("/api/v1/admin").hasAuthority(Role.ADMIN.name())
                        // .requestMatchers("/api/v1/user").hasAuthority(Role.USER.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // .authenticationProvider(authenticationProvider())
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
