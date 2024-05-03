package be.kuleuven.foodrestservice.domain;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class VegetableRepository {
        private static final Map<Integer, Vegetable> vegetables = new HashMap<>();

        @PostConstruct
        public void initData() {
            /*
            Vegetable celery = new Vegetable(1, "Celery", 1.5, 10);
            vegetables.put(celery.getId(), celery);

            Vegetable spinach = new Vegetable(2, "Spinach", 2, 10);
            vegetables.put(spinach.getId(), spinach);

            Vegetable bokchoy = new Vegetable(3, "Bokchoy", 2.5, 10);
            vegetables.put(bokchoy.getId(), bokchoy);

            Vegetable rice = new Vegetable(4, "Rice", 1, 15);
            vegetables.put(rice.getId(), rice);

            Vegetable corn = new Vegetable(5, "Corn", 1, 20);
            vegetables.put(corn.getId(), corn);
            */

            Vegetable beef = new Vegetable(1, "beef", 1.5, 10);
            vegetables.put(beef.getId(), beef);

            Vegetable chicken = new Vegetable(2, "chicken", 2, 10);
            vegetables.put(chicken.getId(), chicken);

            Vegetable cheese = new Vegetable(3, "cheese", 2.5, 10);
            vegetables.put(cheese.getId(), cheese);

            Vegetable milk = new Vegetable(4, "milk", 1, 15);
            vegetables.put(milk.getId(), milk);

            Vegetable fish = new Vegetable(5, "fish", 1, 20);
            vegetables.put(fish.getId(), fish);
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


