package app.exception;

public class StartDateInPast extends RuntimeException {
    public StartDateInPast(String s) {
        super(s);
    }
}
