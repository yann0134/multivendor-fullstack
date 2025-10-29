package com.camoutech.multivendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour représenter un livreur
 * Utilisé pour uniformiser les données entre DeliveryPerson et User avec rôle ROLE_DELIVERY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonDTO {
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String licenseNumber;
    private String vehicleType;
}

