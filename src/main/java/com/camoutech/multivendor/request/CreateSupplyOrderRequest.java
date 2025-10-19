/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateSupplyOrderRequest {
    private Long supplierId;
    private Long warehouseId;
    private List<SupplyOrderItemRequest> items;
    private String notes;
}

