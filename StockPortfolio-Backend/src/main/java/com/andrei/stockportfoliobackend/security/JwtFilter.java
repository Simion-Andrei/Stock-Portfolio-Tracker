package com.andrei.stockportfoliobackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Custom security filter that executes once per HTTP request.
 * <p>
 * Its main responsibility is to intercept the request, extract the JWT token from the "Authorization" header,
 * validate it, and if valid, set the Authentication in the Spring Security Context.
 * </p>
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Internal filter logic.
     * 1. Checks for "Bearer " token in the header.
     * 2. Validates the token signature and expiration.
     * 3. Loads the user details associated with the token.
     * 4. Authenticates the user in the SecurityContext.
     *
     * @param request  The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param chain    The filter chain to proceed with the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String headerAuth = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7); // Remove "Bearer " prefix
            try {
                if (jwtUtils.validateJwtToken(jwt)) {
                    username = jwtUtils.getUsernameFromJwtToken(jwt);
                }
            } catch (Exception e) {
                System.out.println("Could not set user authentication: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}