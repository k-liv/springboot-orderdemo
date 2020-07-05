package eu.acme.demo.service;

import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.domain.enums.OrderStatus;
import eu.acme.demo.repository.OrderItemRepository;
import eu.acme.demo.repository.OrderRepository;
import eu.acme.demo.web.converters.OrderConverter;
import eu.acme.demo.web.converters.OrderItemConverter;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderItemDto;
import eu.acme.demo.web.dto.OrderLiteDto;
import eu.acme.demo.web.dto.OrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderConverter orderConverter;
    private final OrderItemConverter orderItemConverter;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderConverter orderConverter,
                        OrderItemConverter orderItemConverter) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderConverter = orderConverter;
        this.orderItemConverter = orderItemConverter;
    }


    public List<OrderLiteDto> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orderConverter.createFrom(orders);
    }

    public OrderDto findById(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDto> orderItemDtos = orderItemConverter.createFrom(orderItems);
        OrderDto orderDto = orderConverter.createFrom(order);
        orderDto.setOrderItems(orderItemDtos);

        return orderDto;
    }

    @Transactional
    public OrderDto save(OrderRequest orderRequest) {

        Order order = new Order();
        order.setClientReferenceCode(orderRequest.getClientReferenceCode());
        order.setDescription(orderRequest.getDescription());
        order.setItemCount(orderRequest.getOrderItems().size());
        order.setStatus(OrderStatus.SUBMITTED);
        order.setItemTotalAmount(orderRequest.getTotalAmount());
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequest.OrderItem requestItem : orderRequest.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setUnits(requestItem.getUnits());
            orderItem.setUnitPrice(new BigDecimal(requestItem.getUnitPrice()));
            orderItem.setTotalPrice(new BigDecimal(orderItem.getUnits()).multiply(orderItem.getUnitPrice()));
            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        List<OrderItemDto> orderItemDtos = orderItemConverter.createFrom(orderItems);
        OrderDto orderDto = orderConverter.createFrom(order);
        orderDto.setOrderItems(orderItemDtos);

        return orderDto;
    }
}
