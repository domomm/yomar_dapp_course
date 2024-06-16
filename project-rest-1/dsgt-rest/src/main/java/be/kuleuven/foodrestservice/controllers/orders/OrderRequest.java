package be.kuleuven.foodrestservice.controllers.orders;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderRequest {
    private List<OrderItem> orders;
    private String orderRequestId;

    public String getOrderRequestId() {
        return orderRequestId;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public boolean areOrdersUnique() {
        Set<Integer> orderIds = new HashSet<>();
        for (OrderItem orderItem : orders) {
            if (!orderIds.add(orderItem.getId())) {
                return false; // Duplicate order item found
            }
        }
        return true; // All order items are unique
    }
}
