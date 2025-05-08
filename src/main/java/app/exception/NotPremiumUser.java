package app.exception;

public class NotPremiumUser extends RuntimeException {
    public NotPremiumUser(String s) {
        super(s);
    }
}
