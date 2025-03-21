package pl.edu.agh.io_project.boards;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public Board createBoard(Board board) {
        return boardRepository.save(board);
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