package fr.formation.banque.securite;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Joue le rôle du serveur d'autorisation (Keycloak, Auth0, Entra ID...) pour la formation.
 * N'existe JAMAIS dans une vraie API : ici, n'importe qui obtient n'importe quel token.
 */
@RestController
@RequestMapping("/dev")
public class TokenController {

    private final JwtEncoder encoder;

    public TokenController(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    @PostMapping("/token")
    public Map<String, Object> token(@RequestParam String sub, @RequestParam(defaultValue = "") String scope) {
        Instant maintenant = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("banque-formation")
                .subject(sub)
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(1, ChronoUnit.HOURS))
                .claim("scope", scope)
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return Map.of("access_token", token, "token_type", "Bearer", "expires_in", 3600);
    }
}
