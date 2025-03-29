package pl.edu.agh.io_project.projects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.teams.TeamService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;

    @Override
    public Project createProject(ProjectRequest project) {
        var team = teamService.getTeamById(project.teamId());

        var newProject = Project.builder()
                .name(project.name())
                .description(project.description())
                .boards(List.of())
                .team(team)
                .build();

        return projectRepository.save(newProject);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<Project> getProjectsByTeamId(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }

    @Override
    public Project updateProject(Long id, Project updatedProject) {
        Project project = getProjectById(id);
        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
