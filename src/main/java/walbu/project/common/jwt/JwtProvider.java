package walbu.project.common.jwt;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtProvider {

    private final SecretKey key;
    private final Long expiration;

    public JwtProvider(String secret, Long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String createToken(Long memberId) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        Map<String, Object> claims = Map.of("memberId", memberId);

        return Jwts.builder()
                .expiration(expirationDate)
                .claims(claims)
                .signWith(key)
                .compact();
    }

    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parse(token);

    }

}
