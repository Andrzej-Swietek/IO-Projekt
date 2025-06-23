package pl.edu.agh.io_project.users;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserPrincipal implements UserDetails {
    @Getter
    private final String userId;
    @Getter
    private final String email;
    private final String username;
    @Getter
    private final Boolean emailVerified;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(
            String userId,
            String email,
            String username,
            Boolean emailVerified,
            String firstName,
            String lastName,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}