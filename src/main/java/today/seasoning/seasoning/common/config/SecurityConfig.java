package today.seasoning.seasoning.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import today.seasoning.seasoning.user.domain.Role;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .cors().and()
            .formLogin().disable()
            .httpBasic().disable();

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeRequests()
            .antMatchers("/oauth/login/**", "/refresh", "/favicon.ico", "/monitoring/**").permitAll()
            .antMatchers("/manage/**").hasAnyRole(Role.MANAGER.name(), Role.ADMIN.name())
            .antMatchers("/admin/**").hasRole(Role.ADMIN.name())
            .anyRequest().authenticated();

        return httpSecurity.build();
    }
}
