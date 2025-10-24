package com.camoutech.multivendor.request;

import com.camoutech.multivendor.model.Product.ShipmentStatus;
import lombok.Data;

@Data
public class UpdateShipmentStatusRequest {
    private ShipmentStatus status;
}
