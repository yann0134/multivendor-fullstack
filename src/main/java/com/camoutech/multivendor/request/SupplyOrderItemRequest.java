/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.request;

import lombok.Data;

@Data
public class SupplyOrderItemRequest {
    private Long productId;
    private int quantity;
    private double unitPrice;
    private String notes;
}
