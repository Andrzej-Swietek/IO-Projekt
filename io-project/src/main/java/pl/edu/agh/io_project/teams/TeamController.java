package pl.edu.agh.io_project.teams;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.config.annotations.QueryBuilder;
import pl.edu.agh.io_project.config.annotations.QueryBuilderParams;
import pl.edu.agh.io_project.responses.PaginatedResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team")
@AllArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping("/all")
    public PaginatedResponse<Team> getAllTeams(@QueryBuilder QueryBuilderParams query) {
        Page<Team> teamPage = this.teamService.getAllTeams(query.getPageRequest());
        return PaginatedResponse.<Team>builder()
                .data(teamPage.getContent())
                .currentPage(teamPage.getNumber())
                .size(teamPage.getSize())
                .totalCount(teamPage.getTotalElements())
                .build();
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody TeamRequest team) {
        return ResponseEntity.ok(teamService.createTeam(team));
    }

    @PostMapping("/add-team-member")
    public ResponseEntity<TeamMember> addTeamMember(@RequestBody TeamMemberRequest teamMemberRequest) {
        return ResponseEntity.ok(teamService.addTeamMember(teamMemberRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Team>> getTeamsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(teamService.getTeamsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        return ResponseEntity.ok(teamService.updateTeam(id, team));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
