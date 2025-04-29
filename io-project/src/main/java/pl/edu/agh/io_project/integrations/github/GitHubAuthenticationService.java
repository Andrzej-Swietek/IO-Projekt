package pl.edu.agh.io_project.integrations.github;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class GitHubAuthenticationService {

    private final GitHubConfig properties;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateJWT() {
        try {
            PrivateKey privateKey = readPrivateKey();
            Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) privateKey);
            return JWT.create()
                    .withIssuer(String.valueOf(properties.getAppId()))
                    .withIssuedAt(new Date())
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(600)))
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JWT", e);
        }
    }

    public String getInstallationAccessToken(Long installationId) {
        String jwt = generateJWT();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.github.com/app/installations/" + installationId + "/access_tokens",
                HttpMethod.POST,
                request,
                Map.class
        );

        return (String) response.getBody().get("token");
    }

    private PrivateKey readPrivateKey() throws Exception {
        Resource resource = new ClassPathResource(properties.getPrivateKeyPath());
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}
