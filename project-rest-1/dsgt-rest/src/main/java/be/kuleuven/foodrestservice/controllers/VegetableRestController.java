package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Vegetable;
import be.kuleuven.foodrestservice.domain.VegetableRepository;
import be.kuleuven.foodrestservice.exceptions.FailedDecreasingQuantityException;
import be.kuleuven.foodrestservice.exceptions.VegetableNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class VegetableRestController {

    private final VegetableRepository vegetableRepository;

    @Autowired
    VegetableRestController(VegetableRepository vegetableRepository) {
        this.vegetableRepository = vegetableRepository;
    }

    @GetMapping("/vegetables/{id}")
    EntityModel<Vegetable> getVegetableById(@PathVariable int id) {
        Vegetable veg = vegetableRepository.findVegetable(id).orElseThrow(() -> new VegetableNotFoundException(id));

        return vegetableToEntityModel(id, veg);
    }

    @GetMapping("/vegetables")
    CollectionModel<EntityModel<Vegetable>> getVegetables() {

        Collection<Vegetable> vegetables = vegetableRepository.getAllVegetables();
        List<EntityModel<Vegetable>> vegetableEntityModels = new ArrayList<>();

        for (Vegetable v : vegetables) {
            EntityModel<Vegetable> em = vegetableToEntityModel(v.getId(), v);
            vegetableEntityModels.add(em);
        }
        return CollectionModel.of(vegetableEntityModels,
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withSelfRel());
    }

    @GetMapping("/vegetables/decreaseQuantity/{id}/{quantity}")
    EntityModel<Vegetable> decreaseQuantityOfVegetableOfId(@PathVariable int id, @PathVariable int quantity) {
        //Vegetable veg = vegetableRepository.findVegetable(id).orElseThrow(() -> new VegetableNotFoundException(id));
        Optional<Vegetable> vegOptional = vegetableRepository.decreaseQuantity(id, quantity);
        Vegetable veg = vegOptional.orElseThrow(()-> new FailedDecreasingQuantityException(id, quantity));
        EntityModel<Vegetable> em = vegetableToEntityModel(id, veg);
        return em;
    }


    private EntityModel<Vegetable> vegetableToEntityModel(int id, Vegetable veg) {
        return EntityModel.of(veg,
                linkTo(methodOn(VegetableRestController.class).getVegetableById(id)).withSelfRel(),
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withRel("vegetables"),
                Link.of("vegetables/decreaseQuantity/"+id+"/{quantity}")
                        .withType("GET") // Specify the HTTP method
                        .withTitle("Decrease quantity"));
    }

}
