package pl.edu.agh.io_project.boards;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.boards.columns.BoardColumn;
import pl.edu.agh.io_project.boards.columns.BoardColumnRepository;
import pl.edu.agh.io_project.projects.ProjectRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final BoardColumnRepository boardColumnRepository;

    @Override
    @Transactional
    public Board createBoard(BoardRequest board) {
        var project = projectRepository.findById(board.projectId())
                .orElseThrow(NoSuchElementException::new);

        var newBoard = Board.builder()
                .name(board.name())
                .description(board.description())
                .ownerId(board.ownerId())
                .columns(List.of())
                .project(project)
                .build();

        var columns = List.of(
                new BoardColumn(null, "To Do", 1, newBoard, List.of()),
                new BoardColumn(null, "In Progress", 2, newBoard, List.of()),
                new BoardColumn(null, "Done", 3, newBoard, List.of())
        );
        newBoard.setColumns(columns);
        return boardRepository.save(newBoard);
    }

    @Override
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Board not found with id: " + id));
    }

    @Override
    public List<Board> getBoardsByProjectId(Long projectId) {
        return boardRepository.findByProjectId(projectId);
    }

    @Override
    public List<Board> getBoardsByOwnerId(String ownerId) {
        return boardRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Board updateBoard(Long id, Board board) {
        Board existingBoard = getBoardById(id);
        existingBoard.setName(board.getName());
        existingBoard.setOwnerId(board.getOwnerId());
        return boardRepository.save(existingBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new NoSuchElementException("Board not found with id: " + id);
        }
        boardRepository.deleteById(id);
    }
}