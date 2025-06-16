package pl.edu.agh.io_project.teams;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public Page<Team> getAllTeams(PageRequest pageRequest) {
        return teamRepository.findAll(pageRequest);
    }

    @Override
    public Team createTeam(TeamRequest team) {
        Team newTeam = teamRepository.save(
                Team.builder()
                        .name(team.name())
                        .description(team.description())
                        .members(List.of())
                        .projects(List.of())
                        .build()
        );

        // Add creator as ADMIN
        TeamMember creatorMember = TeamMember.builder()
                .userId(team.creatorId())
                .team(newTeam)
                .role(TeamMember.Role.ADMIN)
                .build();
        teamMemberRepository.save(creatorMember);

        return newTeam;
    }

    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Override
    public List<Team> getTeamsByUserId(String userId) {
        return teamRepository.findByMemberUserId(userId);
    }

    @Override
    public Team updateTeam(Long id, Team updatedTeam) {
        Team team = getTeamById(id);
        team.setName(updatedTeam.getName());
        team.setMembers(updatedTeam.getMembers());
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public TeamMember addTeamMember(TeamMemberRequest teamMemberRequest) {
        var team = teamRepository.findById(teamMemberRequest.teamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        var teamMember = TeamMember.builder()
                .userId(teamMemberRequest.teamMember().getUserId())
                .team(team)
                .role(teamMemberRequest.teamMember().getRole())
                .build();

        return teamMemberRepository.save(teamMember);
    }
}