package com.base.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.base.demo.filter.CsrfHeaderFilter;

@Configuration
public class SecurityConfig{
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(authorizeRequests ->
      authorizeRequests
        .requestMatchers(
        "/v3/api-docs/**", 
        "/swagger-ui/**", 
        "/swagger-ui.html").permitAll()
        .requestMatchers(
        "/*/auth/login").permitAll()
        .requestMatchers(
          "/api/v1/auth/register").permitAll()
        .anyRequest().authenticated()
      )
      // .csrf(csrf -> csrf
      //   .csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
      // .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
      .csrf().disable()
      .formLogin((formLogin) ->
      formLogin
        .usernameParameter("email")
        .passwordParameter("password")
        .loginPage("/swagger-ui/index.html")
        .failureUrl("/swagger-ui/index.html?failed")
        .loginProcessingUrl("/auth/login").permitAll());
    return http.build();
  }
  
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CsrfHeaderFilter csrfHeaderFilter() {
    return new CsrfHeaderFilter();
  }
}
