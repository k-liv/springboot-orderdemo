package eu.acme.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.enums.OrderStatus;
import eu.acme.demo.repository.OrderRepository;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderRequest;
import eu.acme.demo.web.error.ErrorCode;
import eu.acme.demo.web.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderAPITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private static final String ITEM_1_PRICE = "14.00";
    private static final String ITEM_2_PRICE = "18.00";
    private static final int ITEM_1_UNITS = 2;
    private static final int ITEM_2_UNITS = 1;
    private static final String CLIENT_REF_CODE = "12345";
    private static final String DESCRIPTION = "test description";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal(ITEM_1_PRICE)
            .multiply(new BigDecimal(ITEM_1_UNITS))
            .add(new BigDecimal(ITEM_2_PRICE))
            .multiply(new BigDecimal(ITEM_2_UNITS));

    @Test
    void testOrderAPI() throws Exception {
        OrderRequest orderRequest = createOrderRequest();

        MvcResult orderResult = performOrder(orderRequest);
        OrderDto orderDto = this.objectMapper.readValue(orderResult.getResponse().getContentAsString(), OrderDto.class);

        Assert.isTrue(orderDto.getDescription().equals(orderRequest.getDescription()), "Wrong description");
        Assert.isTrue(orderDto.getClientReferenceCode().equals(CLIENT_REF_CODE), "Wrong client reference code");
        Assert.isTrue(orderDto.getStatus().equals(OrderStatus.SUBMITTED), "Order status should be submitted");
        Assert.isTrue(orderDto.getOrderItems().size() == 2, "Order should contain 2 items");
        Assert.isTrue(orderDto.getTotalAmount().equals(TOTAL_AMOUNT), "Wrong total amount");
    }

    @Test
    void testOrderDoubleSubmission() throws Exception {
        OrderRequest orderRequest = createOrderRequest();

        performOrder(orderRequest);
        MvcResult orderResult = performOrder(orderRequest);
        ErrorResponse errorResponse = this.objectMapper.readValue(orderResult.getResponse().getContentAsString(), ErrorResponse.class);

        Assert.isTrue(orderResult.getResponse().getStatus() == 400, "Response code should be 400 (bad request)");
        Assert.isTrue(errorResponse.getErrorCode().equals(ErrorCode.DUPLICATE_CLIENT_REF),
                "Response should contain duplicate client reference id error");
    }

    @Test
    void testFetchAllOrders() throws Exception{
        Order order1 = createOrder("5678");
        Order order2 = createOrder("9012");
        orderRepository.save(order1);
        orderRepository.save(order2);

        MvcResult fetchOrdersResult = fetchOrders();
        List<OrderDto> orderDtos = this.objectMapper.readValue(fetchOrdersResult.getResponse().getContentAsString(), new TypeReference<List<OrderDto>>(){});

        Assert.isTrue(orderDtos.size() == 2, "Wrong number of orders");
    }

    @Test
    void testFetchCertainOrder() throws Exception {
        Order order = createOrder(CLIENT_REF_CODE);
        orderRepository.save(order);

        MvcResult fetchOrderResult = fetchOrder(order.getId());
        OrderDto orderDto = this.objectMapper.readValue(fetchOrderResult.getResponse().getContentAsString(), OrderDto.class);

        Assert.isTrue(orderDto.getClientReferenceCode().equals(CLIENT_REF_CODE), "Fetched wrong order");
    }

    @Test
    void testFetchNonExistingOrder() throws Exception{
        MvcResult fetchOrderResult = fetchNonExistingOrder();
        ErrorResponse errorResponse = this.objectMapper.readValue(fetchOrderResult.getResponse().getContentAsString(), ErrorResponse.class);

        Assert.isTrue(fetchOrderResult.getResponse().getStatus() == 400, "Response code should be 400 (bad request)");
        Assert.isTrue(errorResponse.getErrorCode().equals(ErrorCode.INVALID_ORDER_ID),
                "Response should contain invalid order id error");
    }

    private MvcResult performOrder(OrderRequest orderRequest) throws Exception{
        String orderRequestAsString = this.objectMapper.writeValueAsString(orderRequest);
        return this.mockMvc.perform(post("http://localhost:8080/orders")
                .content(orderRequestAsString)
                .contentType("application/json")
                .accept("application/json"))
                .andReturn();
    }

    private MvcResult fetchOrders() throws Exception{
        return this.mockMvc.perform(get("http://localhost:8080/orders")
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult fetchOrder(UUID uuid) throws Exception{
        return this.mockMvc.perform(get("http://localhost:8080/orders/" + uuid)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult fetchNonExistingOrder() throws Exception{
        return this.mockMvc.perform(get("http://localhost:8080/orders/" + UUID.randomUUID())
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private OrderRequest createOrderRequest() {
        List<OrderRequest.OrderItem> orderItems = new ArrayList<>();
        OrderRequest.OrderItem orderItem1 = new OrderRequest.OrderItem();
        orderItem1.setUnitPrice(ITEM_1_PRICE);
        orderItem1.setUnits(ITEM_1_UNITS);
        OrderRequest.OrderItem orderItem2 = new OrderRequest.OrderItem();
        orderItem2.setUnitPrice(ITEM_2_PRICE);
        orderItem2.setUnits(ITEM_2_UNITS);
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setClientReferenceCode(CLIENT_REF_CODE);
        orderRequest.setDescription(DESCRIPTION);
        orderRequest.setOrderItems(orderItems);
        return orderRequest;
    }

    private Order createOrder(String clientRef) {
        Order order = new Order();
        order.setStatus(OrderStatus.SUBMITTED);
        order.setClientReferenceCode(clientRef);
        order.setDescription(DESCRIPTION);
        order.setItemCount(0);
        order.setItemTotalAmount(BigDecimal.valueOf(100));
        return order;
    }
}

