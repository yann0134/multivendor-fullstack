/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.domain.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DeliveryTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String taskId;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order customerOrder;

    @ManyToOne
    @JoinColumn(name = "supply_order_id")
    private SupplyOrder supplyOrder;

    private TaskType type;

    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime assignedDate = LocalDateTime.now();

    private LocalDateTime completedDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

    @OneToOne(cascade = CascadeType.ALL)
    private Address deliveryAddress = new Address();

    private String notes;

    private double estimatedDistance; // en km

    private double estimatedDuration; // en minutes

    private double actualDistance;

    private double actualDuration;
}
