package be.kuleuven.foodrestservice.exceptions;

public class VegetableNotFoundException extends RuntimeException{

    public VegetableNotFoundException(int id) {
        super("Could not find veg " + id);
    }

    public VegetableNotFoundException(){
        super("Could not found veg");
    }
}
