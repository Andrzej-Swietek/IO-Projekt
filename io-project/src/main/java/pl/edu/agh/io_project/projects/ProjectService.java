package pl.edu.agh.io_project.projects;

import java.util.List;

public interface ProjectService {
    Project createProject(ProjectRequest project);

    Project getProjectById(Long id);

    List<Project> getProjectsByTeamId(Long teamId);

    Project updateProject(Long id, Project project);

    void deleteProject(Long id);
}