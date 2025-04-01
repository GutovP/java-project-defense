package flower_shop.exception;

public class NotEnoughInStockException extends RuntimeException {
    public NotEnoughInStockException(String message) {
        super(message);
    }
}
