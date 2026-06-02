package com.gp.order_service.controller;

import com.gp.order_service.common.TransactionRequest;
import com.gp.order_service.common.TransactionResponse;
import com.gp.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/book-order")
    public ResponseEntity<TransactionResponse> bookOrder(@RequestBody TransactionRequest request){
        TransactionResponse saveOrder = orderService.bookOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveOrder);
    }
}
