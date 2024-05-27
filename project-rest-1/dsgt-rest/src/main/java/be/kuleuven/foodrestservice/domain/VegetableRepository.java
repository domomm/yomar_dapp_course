package be.kuleuven.foodrestservice.domain;

import be.kuleuven.foodrestservice.controllers.VegetableRestController;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class VegetableRepository {
        private static final Map<Integer, Vegetable> vegetables = new HashMap<>();
        private static int idGenerator =0;

        @PostConstruct
        public void initData() throws Exception {
            if (VegetableRestController.API_OPENING_STRING.equals("/vegetables")) {
                Vegetable celery = new Vegetable(generateId(), "Celery", 1.5, 100);
                vegetables.put(celery.getId(), celery);
    
                Vegetable spinach = new Vegetable(generateId(), "Spinach", 2, 100);
                vegetables.put(spinach.getId(), spinach);
    
                Vegetable bokchoy = new Vegetable(generateId(), "Bokchoy", 2.5, 100);
                vegetables.put(bokchoy.getId(), bokchoy);
    
                Vegetable tomatoes = new Vegetable(generateId(), "Tomatoes", 1, 150);
                vegetables.put(tomatoes.getId(), tomatoes);
    
                Vegetable cucumber = new Vegetable(generateId(), "Cucumber", 1, 200);
                vegetables.put(cucumber.getId(), cucumber);
    
                Vegetable bellpepper = new Vegetable(generateId(), "Bellpepper", 1, 200);
                vegetables.put(bellpepper.getId(), bellpepper);
    
                Vegetable onions = new Vegetable(generateId(), "Onions", 1, 200);
                vegetables.put(onions.getId(), onions);
    
                Vegetable avocadoes = new Vegetable(generateId(), "Avocadoes", 1, 200);
                vegetables.put(avocadoes.getId(), avocadoes);
    
                Vegetable chestnut = new Vegetable(generateId(), "Chestnut", 1, 100);
                vegetables.put(chestnut.getId(), chestnut);
            } else if (VegetableRestController.API_OPENING_STRING.equals("/animalprods")) {
                Vegetable beef = new Vegetable(generateId(), "Beef", 1.5, 100);
                vegetables.put(beef.getId(), beef);
    
                Vegetable chicken = new Vegetable(generateId(), "Chicken", 2, 100);
                vegetables.put(chicken.getId(), chicken);
    
                Vegetable cheese = new Vegetable(generateId(), "Cheese", 2.5, 100);
                vegetables.put(cheese.getId(), cheese);
    
                Vegetable milk = new Vegetable(generateId(), "Milk", 1, 150);
                vegetables.put(milk.getId(), milk);
    
                Vegetable fish = new Vegetable(generateId(), "Fish", 1, 200);
                vegetables.put(fish.getId(), fish);
            } else if (VegetableRestController.API_OPENING_STRING.equals("/generalstore")) {
                Vegetable rice = new Vegetable(generateId(), "Rice", 1.5, 100);
                vegetables.put(rice.getId(), rice);
    
                Vegetable olive = new Vegetable(generateId(), "Olive Oil", 2, 100);
                vegetables.put(olive.getId(), olive);
    
                Vegetable vodka = new Vegetable(generateId(), "Vodka", 2.5, 100);
                vegetables.put(vodka.getId(), vodka);
    
                Vegetable pasta = new Vegetable(generateId(), "Pasta", 1, 150);
                vegetables.put(pasta.getId(), pasta);
    
                Vegetable water = new Vegetable(generateId(), "Water", 1, 200);
                vegetables.put(water.getId(), water);
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

        public Vegetable addVegetable(Vegetable veg){
            vegetables.put(veg.getId(), veg);
            return vegetables.get(veg.getId());
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

        public int generateId(){
            return idGenerator++;
        }

        public void deleteProduct(int id){
            vegetables.remove(id);
        }

}


