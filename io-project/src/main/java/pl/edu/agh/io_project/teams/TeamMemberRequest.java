package pl.edu.agh.io_project.teams;

import lombok.Data;

public record TeamMemberRequest(
        Long teamId,
        TeamMemberDTO teamMember
) {
}

@Data
class TeamMemberDTO {
    private String userId; // Keycloak User ID
    private TeamMember.Role role;
}