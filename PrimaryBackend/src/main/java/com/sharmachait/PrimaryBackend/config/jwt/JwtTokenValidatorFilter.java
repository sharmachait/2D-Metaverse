package com.sharmachait.PrimaryBackend.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        String jwt = request.getHeader(JwtConstants.JWT_HEADER);
        //Bearer token
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            try{
                SecretKey key = Keys.hmacShaKeyFor(JwtConstants.JWT_SECRET.getBytes());
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();
                String email = String.valueOf(claims.get("email"));
                String auths = String.valueOf(claims.get("authorities"));
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(auths);
                Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // maybe not required
            } catch (JwtException | IllegalArgumentException e) {
                // Handle invalid JWT token (return a 401 Unauthorized response)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                response.getWriter().write("Invalid or expired JWT token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
