package fr.formation.banque.securite;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * L'API est un "resource server" OAuth2 : elle ne gère pas de mots de passe,
 * elle vérifie la signature et le contenu du JWT reçu dans le header Authorization.
 */
@Configuration
public class SecuriteConfig {

    @Value("${securite.jwt.secret}")
    private String secret;

    @Bean
    SecurityFilterChain securite(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                      // API sans cookie de session : pas de CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/dev/token", "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/error").permitAll()
                        // TODO Étape 3 : droits par scope
                        //   GET  /comptes et /comptes/**  -> SCOPE_comptes:lire
                        //   autres méthodes sur /comptes   -> SCOPE_comptes:ecrire
                        //   POST /virements                -> SCOPE_virements:creer
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * Formation : clé HMAC partagée. En production, on supprime ce bean et l'émetteur local,
     * et une seule propriété suffit : spring.security.oauth2.resourceserver.jwt.issuer-uri.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(cle()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(cle()));
    }

    /** Ajoute le bouton Authorize dans Swagger UI. */
    @Bean
    OpenApiCustomizer swaggerBearer() {
        return openApi -> openApi
                .schemaRequirement("bearer", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }

    private SecretKey cle() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
