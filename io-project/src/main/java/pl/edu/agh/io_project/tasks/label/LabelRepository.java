package pl.edu.agh.io_project.tasks.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByTaskId(Long taskId);

    @Query("SELECT l FROM Label l WHERE l.name LIKE %:name%")
    List<Label> searchByName(@Param("name") String name);
}

