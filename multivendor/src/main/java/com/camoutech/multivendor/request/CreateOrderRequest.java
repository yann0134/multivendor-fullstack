package com.camoutech.multivendor.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long addressId;
    private String paymentMethod;
    private String notes;
}
