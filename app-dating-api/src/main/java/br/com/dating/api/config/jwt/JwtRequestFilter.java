package br.com.dating.api.config.jwt;

import br.com.dating.core.domain.user.UserFinderService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserFinderService userFinderService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] PUBLIC_URLS = {
        "/auth/**",
        "/users/**",
        "/h2-console/**",
        "/ws/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/configuration/**",
        "/swagger-ui.html",
        "/webjars/**",
        "/favicon.ico",
        "/.well-known/**"
    };

    public JwtRequestFilter(UserFinderService userFinderService, JwtTokenUtil jwtTokenUtil) {
        this.userFinderService = userFinderService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        System.out.println("Request URI: " + request.getRequestURI());

        boolean isPublicUrl = Arrays.stream(PUBLIC_URLS)
            .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));

        if (isPublicUrl) {
            chain.doFilter(request, response); // ignora a verificação do JWT
            return;
        }

        // Aqui começa a verificação normal do token JWT
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userFinderService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }

}
