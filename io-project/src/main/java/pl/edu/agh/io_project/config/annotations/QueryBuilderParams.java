package pl.edu.agh.io_project.config.annotations;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
public class QueryBuilderParams {
    private final PageRequest pageRequest;

    public QueryBuilderParams(int page, int size, String sort, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        this.pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }
}