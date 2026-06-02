package com.gp.order_service.service;

import com.gp.order_service.client.PaymentFeignClient;
import com.gp.order_service.common.Payment;
import com.gp.order_service.common.TransactionRequest;
import com.gp.order_service.common.TransactionResponse;
import com.gp.order_service.entities.Order;
import com.gp.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentFeignClient paymentFeignClient;
    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String ENDPOINT_URL;

    public OrderService(OrderRepository orderRepository, PaymentFeignClient paymentFeignClient) {
        this.orderRepository = orderRepository;
        this.paymentFeignClient = paymentFeignClient;
    }

//    @CircuitBreaker(name = "paymentServiceCB", fallbackMethod = "paymentFallback")
//    @Retry(name = "paymentServiceRetry", fallbackMethod = "paymentFallback")
    @RateLimiter(name = "paymentServiceRateLimiter", fallbackMethod = "paymentFallback")
    @Transactional
    public TransactionResponse bookOrder(TransactionRequest request){
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        Order saveOrder = orderRepository.save(order);
        payment.setOrderId(saveOrder.getId());
        payment.setAmount(saveOrder.getPrice());
        // rest call for payment service
        ResponseEntity<Payment> paymentResponse = paymentFeignClient.doPayment(payment);
        log.info("Calling PAYMENT-SERVICE");
        log.info("Payment response from payment service : {}", paymentResponse);
        TransactionResponse response = new TransactionResponse();
        if(paymentResponse.getBody()!= null){
            Payment paymentResponseBody = paymentResponse.getBody();
            response.setOrder(saveOrder);
            response.setAmount(paymentResponseBody.getAmount());
            response.setTransactionId(paymentResponseBody.getTransactionId());
        }
        return response;
    }
    public TransactionResponse paymentFallback(
            TransactionRequest request,
            Exception ex) {

        log.error("Payment Service unavailable: {}",
                ex.getMessage());

        Order order = request.getOrder();

        TransactionResponse response =
                new TransactionResponse();

        response.setOrder(order);
        response.setAmount(0.0);
        response.setTransactionId("PAYMENT_PENDING");

        return response;
    }
}
