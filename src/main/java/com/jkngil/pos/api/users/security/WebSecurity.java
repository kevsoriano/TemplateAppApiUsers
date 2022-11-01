package com.jkngil.pos.api.users.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.jkngil.pos.api.users.data.UsersRepository;
import com.jkngil.pos.api.users.service.UsersService;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurity {
	
	@Autowired
	private UsersService usersService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private Environment env;
	@Autowired
	private UsersRepository userRepository;
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        
        http.cors().and().csrf().disable();
		http.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/users").hasIpAddress(env.getProperty("gateway.ip"))
		.antMatchers("/h2-console/**").permitAll()
		.antMatchers(HttpMethod.GET, "/actuator/health").hasIpAddress(env.getProperty("gateway.ip"))
		.antMatchers(HttpMethod.GET, "/actuator/circuitbreakerevents").hasIpAddress(env.getProperty("gateway.ip"))
//		.antMatchers(HttpMethod.DELETE, "users/**").access("hasIpAddress('"+env.getProperty("gateway.ip")+"') and hasAuthority('ROLE_USER')")
		.antMatchers(HttpMethod.DELETE, "users/**").hasIpAddress(env.getProperty("gateway.ip"))
		.anyRequest().authenticated()
		.and()
		.addFilter(getAuthenticationFilter(authenticationManager))
		.addFilter(new AuthorizationFilter(authenticationManager, userRepository, env))
		.authenticationManager(authenticationManager)
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.headers().frameOptions().disable();
		
		return http.build();
	}
	
	private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService,env, authenticationManager);
//		authenticationFilter.setAuthenticationManager(authenticationManager());
		authenticationFilter.setFilterProcessesUrl(env.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-type"));
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
}
