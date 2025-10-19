package com.camoutech.multivendor.service;

import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.domain.TaskType;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.model.DeliveryTask;
import com.camoutech.multivendor.model.Order;
import com.camoutech.multivendor.model.SupplyOrder;

import java.util.List;

public interface DeliveryService {

    DeliveryPerson createDeliveryPerson(DeliveryPerson deliveryPerson) throws Exception;
    DeliveryPerson getDeliveryPersonById(Long id) throws Exception;
    DeliveryPerson getDeliveryPersonByEmail(String email) throws Exception;
    List<DeliveryPerson> getAvailableDeliveryPersons();
    DeliveryPerson updateDeliveryPerson(Long id, DeliveryPerson deliveryPerson) throws Exception;
    void deleteDeliveryPerson(Long id) throws Exception;
    
    // Task management
    DeliveryTask assignPickupTask(SupplyOrder supplyOrder);
    DeliveryTask assignDeliveryTask(Order customerOrder);
    DeliveryTask updateTaskStatus(Long taskId, TaskStatus status) throws Exception;
    List<DeliveryTask> getTasksByDeliveryPerson(Long deliveryPersonId) throws Exception;
    List<DeliveryTask> getTasksByStatus(TaskStatus status);
    List<DeliveryTask> getTasksByType(TaskType type);
    DeliveryTask completeTask(Long taskId, String notes) throws Exception;
}
