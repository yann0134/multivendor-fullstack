/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;

    private String password;

    private String licenseNumber;

    @Embedded
    private VehicleDetails vehicleDetails = new VehicleDetails();

    private DeliveryStatus status = DeliveryStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
    private Address currentLocation = new Address();

    private USER_ROLE role = USER_ROLE.ROLE_DELIVERY;

    private boolean isEmailVerified = false;

    @OneToMany(mappedBy = "deliveryPerson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryTask> deliveryTasks = new ArrayList<>();
}
