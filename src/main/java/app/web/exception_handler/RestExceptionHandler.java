package app.web.exception_handler;

import app.web.ExportController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = ExportController.class)
public class RestExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        return ResponseEntity
                .internalServerError()
                .body(Map.of("error", "Export failed: " + ex.getMessage()));
    }
}