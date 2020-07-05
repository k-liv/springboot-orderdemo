package eu.acme.demo.web;

import eu.acme.demo.service.OrderService;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderLiteDto;
import eu.acme.demo.web.dto.OrderRequest;
import eu.acme.demo.web.error.ErrorCode;
import eu.acme.demo.web.error.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderAPI {

    private final OrderService orderService;

    public OrderAPI(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderLiteDto> fetchOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> fetchOrder(@PathVariable UUID orderId) {
        OrderDto orderDto = orderService.findById(orderId);
        if (orderDto == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(ErrorCode.INVALID_ORDER_ID);
            errorResponse.setErrorMessage("Order ID (orderId) does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> submitOrder(@RequestBody OrderRequest orderRequest) {
        OrderDto orderDto = null;
        try {
            orderDto = orderService.save(orderRequest);
        } catch (DataIntegrityViolationException ex) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(ErrorCode.DUPLICATE_CLIENT_REF);
            errorResponse.setErrorMessage("Client reference code (clientReferenceCode) already exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

}
