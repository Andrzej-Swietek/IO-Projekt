package pl.edu.agh.io_project.reponses;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
