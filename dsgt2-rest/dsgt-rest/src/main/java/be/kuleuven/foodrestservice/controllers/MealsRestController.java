package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class MealsRestController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/rest/meals/{id}")
    EntityModel<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return mealToEntityModel(id, meal);
    }

    @GetMapping("/rest/meals/cheapest")
    EntityModel<Meal> getCheapestMeal(){
        Optional<Meal> mealOptional = mealsRepository.findCheapestMeal();
        Meal meal = mealOptional.orElseThrow(MealNotFoundException::new);
        return mealToEntityModel(meal.getId(), meal);
    }

    @GetMapping("/rest/meals/largest")
    EntityModel<Meal> getLargestMeal(){
        Optional<Meal> mealOptional = mealsRepository.findLargestMeal();
        Meal meal = mealOptional.orElseThrow(MealNotFoundException::new);
        return mealToEntityModel(meal.getId(), meal);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }

    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("rest/meals"));
    }

    @PostMapping("/rest/addMeal")
    public ResponseEntity<EntityModel<Meal>> addMeal(@RequestBody Meal meal) {
        if (meal.getId() == null || !isValidUUID(meal.getId())) {
            String uuid = UUID.randomUUID().toString();
            meal.setId(uuid);
        }

        Meal addedMeal = mealsRepository.addMeal(meal);

        EntityModel<Meal> entityModel = mealToEntityModel(addedMeal.getId(), addedMeal);

        return ResponseEntity.created(URI.create("/rest/meals/" + meal.getId())).body(entityModel);
    }

    @PutMapping("/rest/updateMeal/{id}")
    public ResponseEntity<EntityModel<Meal>> updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        if (mealsRepository.findMeal(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        meal.setId(id);
        mealsRepository.updateMeal(meal);

        EntityModel<Meal> responseModel = mealToEntityModel(id, meal);
        //responseModel.add(WebMvcLinkBuilder.linkTo(MealsRestRpcStyleController.class).slash("rest/meals/" + meal.getId()).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @DeleteMapping("/rest/deleteMeal/{id}")
    public ResponseEntity<CollectionModel<EntityModel<Meal>>> deleteMeal(@PathVariable String id) {
        if (mealsRepository.findMeal(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        mealsRepository.deleteMeal(id);
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel()));
    }

    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
