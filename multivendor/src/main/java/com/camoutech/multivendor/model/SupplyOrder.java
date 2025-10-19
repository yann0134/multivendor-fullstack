/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "supply_orders")
public class SupplyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String supplyOrderId;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "supplyOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyOrderItem> supplyOrderItems = new ArrayList<>();

    private SupplyOrderStatus status = SupplyOrderStatus.PENDING;

    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime deliveryDate;

    private double totalAmount;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
}
