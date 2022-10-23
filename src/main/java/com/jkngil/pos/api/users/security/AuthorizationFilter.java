package com.jkngil.pos.api.users.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.jkngil.pos.api.users.data.UserEntity;
import com.jkngil.pos.api.users.data.UsersRepository;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    
    UsersRepository userRepository;
    Environment env;
    
    public AuthorizationFilter(AuthenticationManager authManager, UsersRepository userRepository, Environment env) {
        super(authManager);
        this.userRepository = userRepository;
        this.env = env;
     }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        
        String header = req.getHeader(env.getProperty("authorization.token.header.name"));
        
        if (header == null || !header.startsWith(env.getProperty("authorization.token.header.prefix"))) {
            chain.doFilter(req, res);
            return;
        }
        
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }   
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(env.getProperty("authorization.token.header.name"));
        
        if (token != null) {
            
            token = token.replace(env.getProperty("authorization.token.header.prefix"), "");
     
            String user = Jwts.parser()
                    .setSigningKey( env.getProperty("token.secret") )
                    .parseClaimsJws( token )
                    .getBody()
                    .getSubject();
            
            if (user != null) {
                UserEntity userEntity = userRepository.findByUserId(user);
            	UserPrincipal userPrincipal = new UserPrincipal(userEntity);
                return new UsernamePasswordAuthenticationToken(user, null, userPrincipal.getAuthorities());
            }
            
            return null;
        }
        
        return null;
    }
   
}