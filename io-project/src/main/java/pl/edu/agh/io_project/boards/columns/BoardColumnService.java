package pl.edu.agh.io_project.boards.columns;

import java.util.List;

public interface BoardColumnService {
    BoardColumn createColumn(BoardColumn column);

    List<BoardColumn> getColumnsByBoardId(Long boardId);

    BoardColumn updateColumn(Long id, BoardColumn column);

    void deleteColumn(Long id);
}
