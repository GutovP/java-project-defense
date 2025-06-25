package flower_shop.exception;

public class AuthorizationDeniedException extends RuntimeException {
    public AuthorizationDeniedException(String message) {
        super(message);
    }
}
