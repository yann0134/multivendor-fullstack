/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :14:35
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.domain.OrderStatus;
import com.camoutech.multivendor.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "order_number")
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;

    private Long sellerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Address shippingAddress;

    @Embedded
    private PaymentDetails paymentDetails = new PaymentDetails();

    private double totalMrpPrice;

    private Integer totalSellingPrice;

    private Integer discount;

    private OrderStatus orderStatus;

    private int totalItem;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    
    // ID de l'utilisateur livreur (pointe vers la table users)
    @Column(name = "delivery_user_id")
    private Long deliveryUserId;

    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    private LocalDateTime orderDate = LocalDateTime.now();
    private LocalDateTime deliverDate = orderDate.plusDays(7);
    private LocalDateTime deliveryDate;

    private String deliveryNotes;

    @PrePersist
    public void generateOrderId() {
        if (this.orderId == null || this.orderId.isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            this.orderId = "ORD-" + timestamp + "-" + String.format("%04d", (int)(Math.random() * 10000));
        }
    }
}
