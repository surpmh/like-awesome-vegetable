package com.i5e2.likeawesomevegetable.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelInfoRequest {
    private Long cancelUserId;
    private Long cancelOrderAmount;
}
