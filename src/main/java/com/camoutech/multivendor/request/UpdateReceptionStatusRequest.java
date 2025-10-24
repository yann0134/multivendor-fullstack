package com.camoutech.multivendor.request;

import com.camoutech.multivendor.model.Product;
import lombok.Data;

@Data
public class UpdateReceptionStatusRequest {
    private Product.ReceptionStatus status;
}
