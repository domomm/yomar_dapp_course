package be.kuleuven.foodrestservice.exceptions;

public class FailedDecreasingQuantityException extends RuntimeException{
    public FailedDecreasingQuantityException(int id, int amount){
        super("Failed decreasing vegetable of id " + id + ", tried decreasing it by " + amount);
    }
}
