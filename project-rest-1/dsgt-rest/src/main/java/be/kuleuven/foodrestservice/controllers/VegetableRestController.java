package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.controllers.orders.OrderItem;
import be.kuleuven.foodrestservice.controllers.orders.OrderRequest;
import be.kuleuven.foodrestservice.domain.Vegetable;
import be.kuleuven.foodrestservice.domain.VegetableRepository;
import be.kuleuven.foodrestservice.exceptions.FailedDecreasingQuantityException;
import be.kuleuven.foodrestservice.exceptions.VegetableNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class VegetableRestController {

    private final VegetableRepository vegetableRepository;
    public static final String API_OPENING_STRING = "/animalprods";
    private final Link orderLink = linkTo(methodOn(VegetableRestController.class).orderVegetables(null))
            .withSelfRel()
            .withType("POST")
            .withTitle("To order")
            .withMedia("JSON");

    @Autowired
    VegetableRestController(VegetableRepository vegetableRepository) throws Exception {
        this.vegetableRepository = vegetableRepository;
    }

    @GetMapping(API_OPENING_STRING+"/{id}")
    ResponseEntity<?> getVegetableById(@PathVariable int id) {
        try {
            Vegetable veg = vegetableRepository.findVegetable(id).orElseThrow(() -> new VegetableNotFoundException(id));
            return ResponseEntity.ok().body(vegetableToEntityModel(id, veg));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Another error occurred: " + e.getMessage());
        }
    }

    @GetMapping(API_OPENING_STRING)
    CollectionModel<EntityModel<Vegetable>> getVegetables() {

        Collection<Vegetable> vegetables = vegetableRepository.getAllVegetables();
        List<EntityModel<Vegetable>> vegetableEntityModels = new ArrayList<>();

        for (Vegetable v : vegetables) {
            EntityModel<Vegetable> em = vegetableToEntityModel(v.getId(), v);
            vegetableEntityModels.add(em);
        }
        return CollectionModel.of(vegetableEntityModels,
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withSelfRel(),
                linkTo(methodOn(VegetableRestController.class).orderVegetables(null))
                        .withSelfRel()
                        .withType("POST")
                        .withTitle("To order")
                        .withMedia("JSON"));
    }

    @GetMapping(API_OPENING_STRING+"/decreaseQuantity/{id}/{quantity}")
    ResponseEntity<?> decreaseQuantityOfVegetableOfId(@PathVariable int id, @PathVariable int quantity) {
        try {
            //Vegetable veg = vegetableRepository.findVegetable(id).orElseThrow(() -> new VegetableNotFoundException(id));
            Optional<Vegetable> vegOptional = vegetableRepository.decreaseQuantity(id, quantity);
            Vegetable veg = vegOptional.orElseThrow(()-> new FailedDecreasingQuantityException(id, quantity));
            EntityModel<Vegetable> em = vegetableToEntityModel(id, veg);
            return ResponseEntity.ok().body(em);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }

    @PostMapping(API_OPENING_STRING+"/order")
    public ResponseEntity<?> orderVegetables(@RequestBody OrderRequest orderRequest) {
        List<OrderItem> orders = orderRequest.getOrders();
        List<EntityModel<Vegetable>> vegetableEntityModels = new ArrayList<>();

        try {
            if (!orderRequest.areOrdersUnique()){
                throw new Exception("Order ids are not unique");
            }
            // Check if everything is okay
            for (OrderItem order : orders) {
                Vegetable veg = vegetableRepository.findVegetable(order.getId())
                        .orElseThrow(VegetableNotFoundException::new);
                if (veg.getQuantity() < order.getQuantity()) {
                    throw new FailedDecreasingQuantityException(veg.getId(), order.getQuantity());
                }
            }

            // Actually changing it if it is okay
            for (OrderItem order : orders) {
                vegetableRepository.decreaseQuantity(order.getId(), order.getQuantity());
                Vegetable v = vegetableRepository.findVegetable(order.getId())
                        .orElseThrow(VegetableNotFoundException::new);
                EntityModel<Vegetable> em = vegetableToEntityModel(v.getId(), v);
                vegetableEntityModels.add(em);
            }

            return ResponseEntity.ok().body(CollectionModel.of(vegetableEntityModels,
                    linkTo(methodOn(VegetableRestController.class).getVegetables()).withSelfRel(),
                    linkTo(methodOn(VegetableRestController.class).orderVegetables(null))
                            .withSelfRel()
                            .withType("POST")
                            .withTitle("To order")
                            .withMedia("JSON")));
        } catch (VegetableNotFoundException | FailedDecreasingQuantityException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occured: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Another error occurred: " + e.getMessage());
        }
    }


    private EntityModel<Vegetable> vegetableToEntityModel(int id, Vegetable veg) {
        return EntityModel.of(veg,
                linkTo(methodOn(VegetableRestController.class).getVegetableById(id)).withSelfRel(),
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withRel("vegetables"));
    }

}
