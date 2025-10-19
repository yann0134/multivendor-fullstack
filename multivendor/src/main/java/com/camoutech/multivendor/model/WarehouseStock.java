/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int reservedQuantity;

    private int availableQuantity;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    private double costPrice; // Prix d'achat au fournisseur

    private double sellingPrice; // Prix de vente au client

    private String locationInWarehouse; // A1-B2-C3 par exemple

    private LocalDateTime expiryDate; // Pour les produits périssables
}
