package com.camoutech.multivendor.request;

import com.camoutech.multivendor.model.Address;
import com.camoutech.multivendor.model.BusinessDetails;
import lombok.Data;

@Data
public class SupplierProfileUpdateRequest {
    
    // Informations de base
    private String supplierName;
    private String mobile;
    
    // Adresse de ramassage
    private Address pickupAddress;
    
    // Détails de l'entreprise
    private BusinessDetails businessDetails;
}
