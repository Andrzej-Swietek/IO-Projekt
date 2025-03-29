package pl.edu.agh.io_project.boards;

import pl.edu.agh.io_project.boards.columns.ColumnOrderItem;

import java.util.List;

public record ReorderBoardRequest(
        List<ColumnOrderItem> orderList
) {
}

