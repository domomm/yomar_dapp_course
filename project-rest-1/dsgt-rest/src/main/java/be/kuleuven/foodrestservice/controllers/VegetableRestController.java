package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.controllers.orders.OrderItem;
import be.kuleuven.foodrestservice.controllers.orders.OrderRequest;
import be.kuleuven.foodrestservice.domain.Vegetable;
import be.kuleuven.foodrestservice.domain.VegetableRepository;
import be.kuleuven.foodrestservice.exceptions.FailedDecreasingQuantityException;
import be.kuleuven.foodrestservice.exceptions.MissingBrokerHeaderException;
import be.kuleuven.foodrestservice.exceptions.VegetableNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class VegetableRestController {

    private final VegetableRepository vegetableRepository;
    public static final String API_OPENING_STRING = "/animalprods";
    private final Link orderLink = linkTo(methodOn(VegetableRestController.class).orderVegetables(null, null))
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
                linkTo(methodOn(VegetableRestController.class).orderVegetables(null, null))
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

    private void addQuantity(int id, String broker) throws Exception {
        String combinedId = broker + "/" + id;
        OrderRequest orderRequest = vegetableRepository.getOrder(combinedId);    
        if (orderRequest == null) {
            throw new Exception("Order with ID " + combinedId + " does not exist.");
        }
    
        List<OrderItem> orders = orderRequest.getOrders();
        for (OrderItem order : orders) {
            vegetableRepository.increaseQuantity(order.getId(), order.getQuantity());
        }
    
        vegetableRepository.removeOrder(combinedId);
    }
    

    @PostMapping(API_OPENING_STRING+"/order")
    public ResponseEntity<?> orderVegetables(
        @RequestBody OrderRequest orderRequest,
        @RequestHeader("broker") String broker) {
        List<OrderItem> orders = orderRequest.getOrders();
        List<EntityModel<Vegetable>> vegetableEntityModels = new ArrayList<>();

        try {
            if (broker == null || broker.isEmpty()) {
                throw new MissingBrokerHeaderException();
            }
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
            
            // Get the stringid for the map
            String orderIdString = String.valueOf(orderRequest.getOrderRequestId());
            String combinedId = broker + "/" + orderIdString;
            // Add order request to map<id, orderrequest>
            vegetableRepository.addOrder(combinedId, orderRequest);

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
                    linkTo(methodOn(VegetableRestController.class).orderVegetables(null, null))
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

    @GetMapping(API_OPENING_STRING + "/removeOrder/{id}")
    public ResponseEntity<?> removeOrder(
        @PathVariable int id,
        @RequestHeader("broker") String broker) {
    try {
        if (broker == null || broker.isEmpty()) {
            throw new MissingBrokerHeaderException();
        }
        addQuantity(id, broker);
        return ResponseEntity.ok().body("Order removed and quantities increased successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }
}

    @PostMapping(API_OPENING_STRING+"/addProduct")
    public ResponseEntity<EntityModel<Vegetable>> addProduct(@RequestBody Vegetable product) {
        
        product.setId(vegetableRepository.generateId());

        Vegetable addedProduct = vegetableRepository.addVegetable(product);

        EntityModel<Vegetable> entityModel = vegetableToEntityModel(addedProduct.getId(), addedProduct);

        return ResponseEntity.ok().body(entityModel);
    }

    @PutMapping(API_OPENING_STRING+"/updateProduct/{id}")
    public ResponseEntity<EntityModel<Vegetable>> updateProduct(@PathVariable int id, @RequestBody Vegetable product) {
        if (vegetableRepository.findVegetable(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        vegetableRepository.addVegetable(product);

        EntityModel<Vegetable> responseModel = vegetableToEntityModel(id, product);
        //responseModel.add(WebMvcLinkBuilder.linkTo(MealsRestRpcStyleController.class).slash("rest/meals/" + meal.getId()).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @DeleteMapping(API_OPENING_STRING+"/deleteProduct/{id}")
    public ResponseEntity<CollectionModel<EntityModel<Vegetable>>> deleteProduct(@PathVariable int id) {
        if (vegetableRepository.findVegetable(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        
        vegetableRepository.deleteProduct(id);
        Collection<Vegetable> vegs = vegetableRepository.getAllVegetables();
        List<EntityModel<Vegetable>> vegEntityModels = new ArrayList<>();
        for (Vegetable v : vegs) {
            EntityModel<Vegetable> em = vegetableToEntityModel(v.getId(), v);
            vegEntityModels.add(em);
        }

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(vegEntityModels,
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withSelfRel()));
    }


    private EntityModel<Vegetable> vegetableToEntityModel(int id, Vegetable veg) {
        return EntityModel.of(veg,
                linkTo(methodOn(VegetableRestController.class).getVegetableById(id)).withSelfRel(),
                linkTo(methodOn(VegetableRestController.class).getVegetables()).withRel("vegetables"));
    }

}
