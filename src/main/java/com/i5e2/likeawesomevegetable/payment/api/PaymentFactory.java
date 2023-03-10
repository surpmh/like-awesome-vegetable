package com.i5e2.likeawesomevegetable.payment.api;

import com.i5e2.likeawesomevegetable.payment.api.dto.AdminTransferResponse;
import com.i5e2.likeawesomevegetable.payment.api.dto.PaymentCardResponse;
import com.i5e2.likeawesomevegetable.payment.api.dto.PaymentRefundResponse;
import com.i5e2.likeawesomevegetable.payment.order.AdminPaymentOrder;
import com.i5e2.likeawesomevegetable.payment.order.UserPaymentOrder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentFactory {
    public static Payment createPayment(PaymentCardResponse paymentCardResponse, UserPaymentOrder userPaymentOrder) {
        return Payment.AddUserPaymentOrder()
                .id(paymentCardResponse.getPaymentKey())
                .userPaymentOrder(userPaymentOrder)
                .paymentAmount(paymentCardResponse.getTotalAmount())
                .paymentMethod(paymentCardResponse.getMethod())
                .paymentStatus(paymentCardResponse.getStatus())
                .paymentType(paymentCardResponse.getType())
                .paymentRequestedAt(paymentCardResponse.getRequestedAt())
                .paymentApprovedAt(paymentCardResponse.getApprovedAt())
                .paymentOrderName(paymentCardResponse.getOrderName())
                .build();
    }

    public static Payment createTransfer(AdminTransferResponse adminTransferResponse, AdminPaymentOrder adminPaymentOrder) {
        return Payment.AddAdminPaymentOrder()
                .id(adminTransferResponse.getPaymentKey())
                .adminPaymentOrder(adminPaymentOrder)
                .paymentAmount(adminTransferResponse.getTotalAmount())
                .paymentMethod(adminTransferResponse.getMethod())
                .paymentStatus(adminTransferResponse.getStatus())
                .paymentType(adminTransferResponse.getType())
                .paymentRequestedAt(adminTransferResponse.getRequestedAt())
                .paymentApprovedAt(adminTransferResponse.getApprovedAt())
                .paymentOrderName(adminTransferResponse.getOrderName())
                .build();
    }

    public static Payment createCancel(PaymentRefundResponse paymentRefundResponse, UserPaymentOrder userCancelOrder) {
        return Payment.AddUserPaymentOrder()
                .id(paymentRefundResponse.getPaymentKey())
                .userPaymentOrder(userCancelOrder)
                .paymentAmount(paymentRefundResponse.getTotalAmount())
                .paymentMethod(paymentRefundResponse.getMethod())
                .paymentStatus(paymentRefundResponse.getStatus())
                .paymentType(paymentRefundResponse.getType())
                .paymentRequestedAt(paymentRefundResponse.getRequestedAt())
                .paymentApprovedAt(paymentRefundResponse.getApprovedAt())
                .paymentOrderName(paymentRefundResponse.getOrderName())
                .build();
    }
}
