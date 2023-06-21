package com.bronyst.springjwtroles.security;

import com.bronyst.springjwtroles.security.jwt.AuthEntryPointJwt;
import com.bronyst.springjwtroles.security.jwt.AuthTokenFilter;
import com.bronyst.springjwtroles.serviceimpl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }

//    bcrypt password encoder
    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return  authenticationProvider;
    }
   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
       httpSecurity.csrf(csrf -> csrf.disable())
               .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authorizeHttpRequests(auth ->
                       auth.requestMatchers("/api/auth/**").permitAll()
                               .requestMatchers("/api/test/**").permitAll()
                               .anyRequest().authenticated()
               );
       httpSecurity.authenticationProvider(daoAuthenticationProvider());
       httpSecurity.addFilterAt(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
       return httpSecurity.build();
    }


}
