package com.camoutech.multivendor.request;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private SupplyOrderStatus status;
}
