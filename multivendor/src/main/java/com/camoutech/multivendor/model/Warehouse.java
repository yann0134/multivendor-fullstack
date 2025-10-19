/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.WarehouseStatus;
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
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String warehouseName;

    @OneToOne(cascade = CascadeType.ALL)
    private Address location = new Address();

    private WarehouseStatus status = WarehouseStatus.ACTIVE;

    private double capacity; // en m³

    private double currentStock; // en m³

    private String description;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseStock> warehouseStocks = new ArrayList<>();
}
