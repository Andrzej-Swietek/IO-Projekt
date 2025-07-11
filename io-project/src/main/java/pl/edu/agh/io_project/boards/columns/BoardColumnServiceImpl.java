package pl.edu.agh.io_project.boards.columns;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.boards.BoardRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardColumnServiceImpl implements BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    @Override
    public BoardColumn createColumn(BoardColumnRequest column) {
        var board = boardRepository.findById(column.boardId())
                .orElseThrow(() -> new NoSuchElementException("Board not found with id: " + column.boardId()));

        return columnRepository.save(BoardColumn.builder()
                .name(column.name())
                .tasks(List.of())
                .board(board)
                .build());
    }

    @Override
    public List<BoardColumn> getColumnsByBoardId(Long boardId) {
        return columnRepository.findByBoardIdOrderByPosition(boardId);
    }

    @Override
    @Transactional
    public BoardColumn updateColumn(Long id, BoardColumn column) {
        BoardColumn existingColumn = columnRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Column not found with id: " + id));
        existingColumn.setName(column.getName());
        existingColumn.setPosition(column.getPosition());
        return columnRepository.save(existingColumn);
    }

    @Override
    @Transactional
    public void deleteColumn(Long id) {
        if (!columnRepository.existsById(id)) {
            throw new NoSuchElementException("Column not found with id: " + id);
        }
        columnRepository.deleteById(id);
    }
}