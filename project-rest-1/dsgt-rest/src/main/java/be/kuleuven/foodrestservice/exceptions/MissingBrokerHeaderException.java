package be.kuleuven.foodrestservice.exceptions;

public class MissingBrokerHeaderException extends RuntimeException {
    public MissingBrokerHeaderException() {
        super("Missing broker header.");
    }
}
