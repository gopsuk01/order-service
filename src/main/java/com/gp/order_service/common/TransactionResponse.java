package com.gp.order_service.common;

import com.gp.order_service.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Order order;
    private Double amount;
    private String transactionId;
}
