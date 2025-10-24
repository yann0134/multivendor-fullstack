/**
 * Created by camoutech
 * Date :19/10/2024
 * Time :15:30
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WarehouseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String fullName;
    private String mobile;
    private String password;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_WAREHOUSE;

    private String warehouseName;
    private String warehouseLocation;
    private String warehouseCode;
    private String managerName;
    private String contactPhone;
    private String address;
    private String city;
    private String region;
    private String country = "Cameroun";

    // Statut du compte
    private String accountStatus = "ACTIVE";

    // Informations de gestion
    private String department;
    private String position;
    private String employeeId;
    private String supervisorEmail;

    // Capacité de stockage
    private Double storageCapacity; // en m²
    private Integer maxProducts;
    private String storageType; // REFRIGERATED, AMBIENT, FROZEN, etc.

    // Horaires de travail
    private String workingHours;
    private String workingDays;
    private String timeZone = "Africa/Douala";

    // Permissions spéciales
    private Boolean canManageInventory = true;
    private Boolean canProcessOrders = true;
    private Boolean canGenerateReports = true;
    private Boolean canManageSuppliers = false;
    private Boolean canManageDelivery = false;

    // Métadonnées
    private String createdBy;
    private String lastModifiedBy;
    private String notes;
    private Boolean isActive = true;
}
