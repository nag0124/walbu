package walbu.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import walbu.project.common.jwt.JwtProvider;

@Configuration
public class JwtProviderConfig {

    @Bean
    public JwtProvider jwtProvider(@Value("${jwt.secret}") String secret,
                                   @Value("${jwt.expiration}") long expiration) {
        return new JwtProvider(secret, expiration);
    }

}
