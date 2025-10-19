/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.domain.TaskType;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.model.DeliveryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {
    
    List<DeliveryTask> findByDeliveryPerson(DeliveryPerson deliveryPerson);
    
    List<DeliveryTask> findByStatus(TaskStatus status);
    
    List<DeliveryTask> findByType(TaskType type);
    
    List<DeliveryTask> findByDeliveryPersonAndStatus(DeliveryPerson deliveryPerson, TaskStatus status);
    
    List<DeliveryTask> findByAssignedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
