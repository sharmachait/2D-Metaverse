package com.sharmachait.ws.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtProvider {

  public static String getEmailFromToken(String token) throws Exception {
    if (token == null || !token.startsWith("Bearer "))
      throw new Exception("Invalid JWT token");
    String jwt = token.substring(7);
    try {
      SecretKey key = Keys.hmacShaKeyFor(JwtConstants.JWT_SECRET.getBytes());
      Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
      return String.valueOf(claims.get("email"));
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  public static String getIdFromToken(String token) {
    if (token == null || !token.startsWith("Bearer "))
      return null;
    String jwt = token.substring(7);
    try {
      SecretKey key = Keys.hmacShaKeyFor(JwtConstants.JWT_SECRET.getBytes());
      Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
      String email = String.valueOf(claims.get("email"));
      String id = String.valueOf(claims.get("id"));
      return String.valueOf(claims.get("id"));
    } catch (Exception e) {
      String message = e.getMessage();
      return null;
    }
  }

}
