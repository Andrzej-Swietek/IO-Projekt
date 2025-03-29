package pl.edu.agh.io_project.boards.columns;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByBoardIdOrderByPosition(Long boardId);

    boolean existsById(@NonNull Long id);
}