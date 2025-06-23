package pl.edu.agh.io_project.users;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class JwtToUserPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String CLIENT_ID = "kanban-backend";

    public AbstractAuthenticationToken convert(Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String username = jwt.getClaimAsString("preferred_username");

        Set<String> roles = extractRoles(jwt);

        Set<GrantedAuthority> authorities = roles.stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toSet());

        UserPrincipal userPrincipal = new UserPrincipal(
                userId,
                email,
                username,
                emailVerified,
                firstName,
                lastName,
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                jwt.getTokenValue(),
                authorities
        );
    }

    private Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess instanceof Map<?, ?> raMap) {
            Object realmRoles = raMap.get("roles");
            if (realmRoles instanceof Collection<?> rr) {
                rr.forEach(role -> roles.add(role.toString()));
            }
        }

        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess instanceof Map<?, ?> resMap) {
            Object clientAccess = resMap.get(CLIENT_ID);
            if (clientAccess instanceof Map<?, ?> clientMap) {
                Object clientRoles = clientMap.get("roles");
                if (clientRoles instanceof Collection<?> cr) {
                    cr.forEach(role -> roles.add(role.toString()));
                }
            }
        }
        return roles;
    }
}
