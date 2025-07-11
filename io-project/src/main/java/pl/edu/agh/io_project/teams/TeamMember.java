package pl.edu.agh.io_project.teams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // Keycloak User ID

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "team_id")
    private Team team;

    public enum Role {
        MEMBER, MANAGER, ADMIN, OWNER
    }
}
