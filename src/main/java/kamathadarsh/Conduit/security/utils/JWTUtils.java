package kamathadarsh.Conduit.security.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import kamathadarsh.Conduit.security.securityDTO.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Date;

@Component
@AllArgsConstructor
public class JWTUtils {

    private final KeyUtils keyUtils;

    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
    public String generateJWTToken(Authentication authentication) throws Exception {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .signWith(keyUtils.loadPrivateKey())
                .compact();
    }

    public boolean validateJWTToken(String JWTToken){

        try {
            PublicKey publicKey = keyUtils.loadPublicKey();

            Jwts.parserBuilder().setSigningKey(publicKey).build().parse(JWTToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("error in getting public key: {}", e.getMessage());
        }

        return false;
    }

    public String getUsernameFromJWTToken(String JWTToken) throws Exception {

        PublicKey publicKey = keyUtils.loadPublicKey();
        String username = Jwts.parserBuilder().setSigningKey(publicKey)
                .build()
                .parseClaimsJws(JWTToken)
                .getBody().getSubject();

        return username;
    }



}
