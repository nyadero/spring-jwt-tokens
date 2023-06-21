package com.bronyst.springjwtroles.security.jwt;

import com.bronyst.springjwtroles.service.UserDetailsImp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;

@Component
public class JwtUtils {
    public static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${bronyst.app.jwtSecret}")
    private String jwtSecret;
    @Value("${bronyst.app.jwtExpirationMs}")
    private int jwtExpirations;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImp userPricipal = (UserDetailsImp) authentication.getPrincipal();
        return builder()
                .setSubject(userPricipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirations))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
