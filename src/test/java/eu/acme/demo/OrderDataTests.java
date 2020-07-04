package eu.acme.demo;


import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.domain.enums.OrderStatus;
import eu.acme.demo.repository.OrderItemRepository;
import eu.acme.demo.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class OrderDataTests {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void testCreateOrder() {
        Order order = createOrder();
        orderRepository.save(order);

        Assert.isTrue(orderRepository.findById(order.getId()).isPresent(), "order not found");
        Assert.isTrue(!orderRepository.findById(UUID.randomUUID()).isPresent(), "non existing order found");
    }

    @Test
    public void testCreateOrderItems() {
        Order order = createOrder();
        orderRepository.save(order);
        OrderItem orderItem1 = createOrderItem(order, 1, BigDecimal.valueOf(10.00));
        orderItemRepository.save(orderItem1);
        OrderItem orderItem2 = createOrderItem(order, 9, BigDecimal.valueOf(10.00));
        orderItemRepository.save(orderItem2);

        Assert.isTrue(orderItemRepository.findById(orderItem1.getId()).isPresent(), "order item not found");
        Assert.isTrue(orderItemRepository.findByOrder(order).size() == 2, "order items not found in order");
    }

    @Test
    public void testCreateOrderItemWithoutOrder_ShouldThrowException() {
        OrderItem orderItem = createOrderItem(null, 1, BigDecimal.valueOf(10));

        Assertions.assertThrows(DataIntegrityViolationException.class, ()-> orderItemRepository.save(orderItem));
    }

    private Order createOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.SUBMITTED);
        order.setClientReferenceCode("ORDER-1");
        order.setDescription("first order");
        order.setItemCount(10);
        order.setItemTotalAmount(BigDecimal.valueOf(100.23));
        return order;
    }

    private OrderItem createOrderItem(Order order, int units, BigDecimal unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setUnits(units);
        orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(units)));
        return orderItem;
    }

}
