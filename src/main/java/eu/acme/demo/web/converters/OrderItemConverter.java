package eu.acme.demo.web.converters;

import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.web.dto.OrderItemDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemConverter {

    public List<OrderItemDto> createFrom(List<OrderItem> orderItems) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setItemId(orderItem.getId());
            orderItemDto.setTotalPrice(orderItem.getTotalPrice());
            orderItemDto.setUnitPrice(orderItem.getUnitPrice());
            orderItemDto.setUnits(orderItem.getUnits());
            orderItemDtos.add(orderItemDto);
        }
        return orderItemDtos;
    }
}
