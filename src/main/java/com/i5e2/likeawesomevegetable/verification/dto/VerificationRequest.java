package com.i5e2.likeawesomevegetable.verification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    private String businessNo;
    private String startDate;
    private String managerName;
}
