package pl.edu.agh.io_project.boards;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByProjectId(Long projectId);
    List<Board> findByOwnerId(String ownerId);
}