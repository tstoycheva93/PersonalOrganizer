package app.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String categoryNotFound) {
        super(categoryNotFound);
    }
}
