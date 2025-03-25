package pl.edu.agh.io_project.boards;

import java.util.List;

public interface BoardService {
    Board createBoard(BoardRequest board);

    Board getBoardById(Long id);

    List<Board> getBoardsByProjectId(Long projectId);

    List<Board> getBoardsByOwnerId(String ownerId);

    Board updateBoard(Long id, Board board);

    Board reorderBoardColumns(Long boardId, ReorderBoardRequest request);

    void deleteBoard(Long id);
}