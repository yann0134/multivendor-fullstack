package com.camoutech.multivendor.request;

import lombok.Data;

@Data
public class SupplyOrderItemRequest {
    private Long productId;
    private int quantity;
    private double unitPrice;
}