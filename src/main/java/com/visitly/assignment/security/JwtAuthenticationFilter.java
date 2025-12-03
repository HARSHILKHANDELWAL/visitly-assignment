package com.visitly.assignment.security;

import com.visitly.assignment.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    JwtAuthEntryPoint authEntryPoint;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        String token = null;

        String path = req.getServletPath();

        // Exclude specific endpoints
        if (path.equals("/api/users/login") ||
                path.equals("/api/users/register")) {
            chain.doFilter(req, res);
            return;
        }
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        if(token==null){
            authEntryPoint.commence(req, res, new AuthenticationException("Missing Authorization toker") {
            });
            return;
        }

        try {
            if (token != null && jwtUtils.validateJwtToken(token)) {
                String email = jwtUtils.getEmailFromJwt(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(req, res);
        } catch (Exception e) {
            authEntryPoint.commence(req, res, new AuthenticationException(e.getMessage()) {
            });
        }
    }


}