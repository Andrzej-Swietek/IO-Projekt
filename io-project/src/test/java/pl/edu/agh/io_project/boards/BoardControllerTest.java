package pl.edu.agh.io_project.boards;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.io_project.boards.columns.ColumnOrderItem;
import pl.edu.agh.io_project.projects.Project;
import pl.edu.agh.io_project.teams.Team;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BoardController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        })
@Import(BoardControllerTest.TestConfig.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BoardService boardService() {
            return Mockito.mock(BoardService.class);
        }
    }

    private Board sampleBoard() {
        return Board.builder()
                .id(1L)
                .name("Test Board")
                .description("Test description")
                .ownerId("owner-1")
                .columns(List.of())
                .project(
                        Project.builder()
                                .name("test-project")
                                .id(1L)
                                .description("test project description")
                                .team(
                                        Team.builder()
                                                .name("team")
                                                .id(1L)
                                                .description("test team")
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    @Test
    void shouldCreateBoard() throws Exception {
        BoardRequest request = new BoardRequest("Test Board", "desc", "owner-1", 1L);
        Board response = sampleBoard();

        Mockito.when(boardService.createBoard(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Board"));
    }

    @Test
    void shouldGetBoardById() throws Exception {
        Mockito.when(boardService.getBoardById(1L)).thenReturn(sampleBoard());

        mockMvc.perform(get("/api/v1/board/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Board"));
    }

    @Test
    void shouldGetBoardsByProjectId() throws Exception {
        Mockito.when(boardService.getBoardsByProjectId(1L)).thenReturn(List.of(sampleBoard()));

        mockMvc.perform(get("/api/v1/board/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void shouldGetBoardsByOwnerId() throws Exception {
        Mockito.when(boardService.getBoardsByOwnerId("owner-1")).thenReturn(List.of(sampleBoard()));

        mockMvc.perform(get("/api/v1/board/owner/owner-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void shouldReorderColumns() throws Exception {
        ColumnOrderItem item1 = new ColumnOrderItem(3L, 0);
        ColumnOrderItem item2 = new ColumnOrderItem(4L, 1);
        ReorderBoardRequest reorderRequest = new ReorderBoardRequest(List.of(item1, item2));

        Mockito.when(boardService.reorderBoardColumns(eq(1L), any())).thenReturn(sampleBoard());

        mockMvc.perform(patch("/api/v1/board/reorder-columns/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateBoard() throws Exception {
        Board update = sampleBoard();
        update.setName("Updated Board");

        Mockito.when(boardService.updateBoard(eq(1L), any())).thenReturn(update);

        mockMvc.perform(put("/api/v1/board/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Board"));
    }

    @Test
    void shouldDeleteBoard() throws Exception {
        mockMvc.perform(delete("/api/v1/board/1"))
                .andExpect(status().isNoContent());
    }
}
