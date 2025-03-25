package pl.edu.agh.io_project.responses;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
