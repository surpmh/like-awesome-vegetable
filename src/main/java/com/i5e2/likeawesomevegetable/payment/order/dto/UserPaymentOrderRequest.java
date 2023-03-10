package com.i5e2.likeawesomevegetable.payment.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentOrderRequest {
    private String postTitle;
    private Long userId;
    private Long paymentOrderAmount;
}
