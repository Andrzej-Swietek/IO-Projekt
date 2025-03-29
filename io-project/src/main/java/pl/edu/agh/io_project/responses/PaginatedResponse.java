package pl.edu.agh.io_project.responses;

import lombok.*;

import java.util.Collections;
import java.util.List;


@Getter
@Setter
@Builder
public class PaginatedResponse<T> {

    private final List<T> data;
    private final int currentPage;
    private final int size;
    private final long totalCount;

    private PaginatedResponse(List<T> data, int currentPage, int size, long totalCount) {
        this.data = data != null ? data : Collections.emptyList();
        this.currentPage = currentPage;
        this.size = size;
        this.totalCount = totalCount;
    }
}