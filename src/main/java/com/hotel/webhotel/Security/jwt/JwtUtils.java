package com.hotel.webhotel.Security.jwt;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.hotel.webhotel.Security.user.UserDetail;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.servlet.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${spring.servlet.security.jwt.expire-length}")
    private int jwtExpirationTime;

    public String generateJwtTokenForUser(Authentication authentication){
        UserDetail userPrincipal = (UserDetail) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationTime))
                .signWith(Key(), SignatureAlgorithm.HS256).compact();
    }

    private Key Key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getuserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

     public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(Key()).build().parse(token);
            return true;
        }catch(MalformedJwtException e){
            logger.error("Invalid jwt token : {} ", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired token : {} ", e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("This token is not supported : {} ", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("No  claims found : {} ", e.getMessage());
        }
        return false;
    }
}
