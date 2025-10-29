package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un livreur
 */
@Entity
@Table(name = "delivery_person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column
    private String licenseNumber;

    @Embedded
    private VehicleDetails vehicleDetails = new VehicleDetails();

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DeliveryStatus status = null; // Nullable pour éviter les problèmes de contrainte

    @OneToOne(cascade = CascadeType.ALL)
    private Address currentLocation = new Address();

    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_DELIVERY;

    @Column
    private boolean isEmailVerified = false;

    @Column
    private Boolean isActive = true;

    @OneToMany(mappedBy = "deliveryPerson", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DeliveryTask> deliveryTasks = new ArrayList<>();

    // Méthodes de compatibilité avec l'ancienne version
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getPhone() {
        return mobile;
    }

    public void setPhone(String phone) {
        this.mobile = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        if (!isActive) {
            this.status = DeliveryStatus.CANCELLED;
        }
    }

    public String getVehicleType() {
        return vehicleDetails != null ? vehicleDetails.getVehicleType() : null;
    }

    public void setVehicleType(String vehicleType) {
        if (vehicleDetails == null) {
            vehicleDetails = new VehicleDetails();
        }
        vehicleDetails.setVehicleType(vehicleType);
    }
}