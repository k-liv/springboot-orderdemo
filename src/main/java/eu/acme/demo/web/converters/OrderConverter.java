package eu.acme.demo.web.converters;

import eu.acme.demo.domain.Order;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderLiteDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderConverter {

    public OrderDto createFrom(Order order) {
        OrderDto orderDto = new OrderDto();
        buildCommonFields(orderDto, order);
        return orderDto;
    }

    public List<OrderLiteDto> createFrom(List<Order> orders) {
        List<OrderLiteDto> orderLiteDtos = new ArrayList<>();
        for (Order order : orders) {
            orderLiteDtos.add(createLite(order));
        }
        return orderLiteDtos;
    }

    private void buildCommonFields(OrderLiteDto orderLiteDto, Order order) {
        orderLiteDto.setId(order.getId());
        orderLiteDto.setClientReferenceCode(order.getClientReferenceCode());
        orderLiteDto.setDescription(order.getDescription());
        orderLiteDto.setStatus(order.getStatus());
        orderLiteDto.setItemCount(order.getItemCount());
        orderLiteDto.setTotalAmount(order.getItemTotalAmount());
    }

    private OrderLiteDto createLite(Order order) {
        OrderLiteDto orderLiteDto = new OrderLiteDto();
        buildCommonFields(orderLiteDto, order);
        return orderLiteDto;
    }
}
