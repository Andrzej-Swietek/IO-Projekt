package pl.edu.agh.io_project.teams;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    public Team createTeam(Team team) {
        return teamRepository.save(team);
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
}