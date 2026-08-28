package com.example.textsocial.social.service;

import com.example.textsocial.social.domain.UserAccount;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final long ttlSeconds;
    public JwtService(JwtEncoder encoder, @Value("${app.jwt.ttl-seconds:3600}") long ttlSeconds) {
        this.encoder = encoder; this.ttlSeconds = ttlSeconds;
    }
    public String issue(UserAccount user) {
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("text-social").issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds)).subject(user.getId().toString())
                .claim("username", user.getUsername()).build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    public long ttlSeconds() { return ttlSeconds; }
}

