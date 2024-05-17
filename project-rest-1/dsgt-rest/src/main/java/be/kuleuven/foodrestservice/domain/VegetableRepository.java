package be.kuleuven.foodrestservice.domain;

import be.kuleuven.foodrestservice.controllers.VegetableRestController;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class VegetableRepository {
        private static final Map<Integer, Vegetable> vegetables = new HashMap<>();

        @PostConstruct
        public void initData() throws Exception {
            if (VegetableRestController.API_OPENING_STRING == "/vegetables"){
                Vegetable celery = new Vegetable(1, "Celery", 1.5, 100);
                vegetables.put(celery.getId(), celery);

                Vegetable spinach = new Vegetable(2, "Spinach", 2, 100);
                vegetables.put(spinach.getId(), spinach);

                Vegetable bokchoy = new Vegetable(3, "Bokchoy", 2.5, 100);
                vegetables.put(bokchoy.getId(), bokchoy);

                Vegetable rice = new Vegetable(4, "tomatoes", 1, 150);
                vegetables.put(rice.getId(), rice);

                Vegetable corn = new Vegetable(5, "cucumber", 1, 200);
                vegetables.put(corn.getId(), corn);

                Vegetable bellpepper = new Vegetable(6, "bellpepper", 1, 200);
                vegetables.put(corn.getId(), corn);

                Vegetable onions = new Vegetable(7, "onions", 1, 200);
                vegetables.put(corn.getId(), corn);

                Vegetable avocadoes = new Vegetable(8, "avocadoes", 1, 200);
                vegetables.put(corn.getId(), corn);
            } else if (VegetableRestController.API_OPENING_STRING == "/animalprods"){
                Vegetable beef = new Vegetable(1, "beef", 1.5, 100);
                vegetables.put(beef.getId(), beef);

                Vegetable chicken = new Vegetable(2, "chicken", 2, 100);
                vegetables.put(chicken.getId(), chicken);

                Vegetable cheese = new Vegetable(3, "cheese", 2.5, 100);
                vegetables.put(cheese.getId(), cheese);

                Vegetable milk = new Vegetable(4, "milk", 1, 150);
                vegetables.put(milk.getId(), milk);

                Vegetable fish = new Vegetable(5, "fish", 1, 200);
                vegetables.put(fish.getId(), fish);
            } else if (VegetableRestController.API_OPENING_STRING == "/generalstore") {
                Vegetable beef = new Vegetable(1, "rice", 1.5, 100);
                vegetables.put(beef.getId(), beef);

                Vegetable chicken = new Vegetable(2, "olive oil", 2, 100);
                vegetables.put(chicken.getId(), chicken);

                Vegetable cheese = new Vegetable(3, "vodka", 2.5, 100);
                vegetables.put(cheese.getId(), cheese);

                Vegetable milk = new Vegetable(4, "pasta", 1, 150);
                vegetables.put(milk.getId(), milk);

                Vegetable fish = new Vegetable(5, "water", 1, 200);
                vegetables.put(fish.getId(), fish);
            } else {
                throw new Exception("Api opening string is not set correctly");
            }
        }

        public Optional<Vegetable> findVegetable(int id) {
            Vegetable veg = vegetables.get(id);
            return Optional.ofNullable(veg);
        }

        public Collection<Vegetable> getAllVegetables() {
            return vegetables.values();
        }

        public Optional<Vegetable> decreaseQuantity(int id, int amount) {
            Optional<Vegetable> vegetableOptional = findVegetable(id);
            if (vegetableOptional.isPresent()) {
                Vegetable veg = vegetableOptional.get();
                int quantBefore = veg.getQuantity();
                if (amount <= quantBefore) {
                    veg.setQuantity(quantBefore - amount);
                    return Optional.of(veg);
                } else {
                    // If amount to decrease exceeds available quantity, return empty optional
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }



}


