package pl.edu.agh.io_project.handlers;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
