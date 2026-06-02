package com.gp.order_service.client;

import com.gp.order_service.common.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentFeignClient {

    @PostMapping("/payment/do-payment")
    ResponseEntity<Payment> doPayment(@RequestBody Payment payment);
}
