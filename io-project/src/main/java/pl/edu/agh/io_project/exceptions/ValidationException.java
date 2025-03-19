package pl.edu.agh.io_project.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    @Override
    public String getMessage() {
        return String.join("\n", errors);
    }
}