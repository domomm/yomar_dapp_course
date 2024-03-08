package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class MealsRestRpcStyleController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestRpcStyleController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/restrpc/meals/{id}")
    Meal getMealById(@PathVariable String id) {
        Optional<Meal> meal = mealsRepository.findMeal(id);

        return meal.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/restrpc/meals")
    Collection<Meal> getMeals() {
        return mealsRepository.getAllMeal();
    }

    @GetMapping("/restrpc/meals/cheapest")
    Meal getCheapestMeal(){
        Optional<Meal> meal = mealsRepository.findCheapestMeal();

        return meal.orElseThrow(MealNotFoundException::new);
    }

    @GetMapping("/restrpc/meals/largest")
    Meal getLargestMeal(){
        Optional<Meal> meal = mealsRepository.findLargestMeal();

        return meal.orElseThrow(MealNotFoundException::new);
    }

    @PostMapping("/restrpc/addMeal")
    public ResponseEntity<String> addMeal(@RequestBody Meal meal) {
        if (meal.getId() != null && mealsRepository.findMeal(meal.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Meal with ID " + meal.getId() + " already exists.");
        }

        mealsRepository.addMeal(meal);

        return ResponseEntity.status(HttpStatus.CREATED).body("Meal added successfully with ID: " + meal.getId());
    }

    @PutMapping("restrpc/updateMeal/{id}")
    public ResponseEntity<String> updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        if (mealsRepository.findMeal(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal with ID " + id + " not found.");
        }

        meal.setId(id);
        mealsRepository.updateMeal(meal);

        return ResponseEntity.status(HttpStatus.OK).body("Meal updated successfully with ID: " + id);
    }

    @DeleteMapping("restrpc/deleteMeal/{id}")
    public ResponseEntity<String> deleteMeal(@PathVariable String id) {
        if (mealsRepository.findMeal(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal with ID " + id + " not found.");
        }

        mealsRepository.deleteMeal(id);

        return ResponseEntity.status(HttpStatus.OK).body("Meal deleted successfully with ID: " + id);
    }
}
