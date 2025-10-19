/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetails {
    
    private String vehicleType; // MOTORCYCLE, CAR, VAN, TRUCK
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
    private double maxCapacity; // en kg
    private String insuranceNumber;
    private String registrationNumber;
}
