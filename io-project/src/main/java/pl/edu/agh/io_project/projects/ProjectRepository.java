package pl.edu.agh.io_project.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeamId(Long teamId);

    @Query("SELECT p FROM Project p WHERE p.name LIKE %:name%")
    List<Project> searchByName(@Param("name") String name);
}