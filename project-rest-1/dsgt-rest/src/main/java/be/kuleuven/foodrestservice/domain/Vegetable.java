package be.kuleuven.foodrestservice.domain;

import java.util.Objects;

public class Vegetable {
    private int id;
    private String name;
    private double price;
    private int quantity;

    public Vegetable(int id, String name, double price, int quantity){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vegetable veg = (Vegetable) o;
        return Objects.equals(id, veg.id) &&
                Objects.equals(name, veg.name) &&
                Objects.equals(price, veg.price) &&
                Objects.equals(quantity, veg.quantity);
    }
}

