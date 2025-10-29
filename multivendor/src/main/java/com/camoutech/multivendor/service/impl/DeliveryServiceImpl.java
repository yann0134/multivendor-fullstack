/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.domain.TaskType;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.model.DeliveryTask;
import com.camoutech.multivendor.model.Order;
import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.repository.DeliveryPersonRepository;
import com.camoutech.multivendor.repository.DeliveryTaskRepository;
import com.camoutech.multivendor.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryTaskRepository deliveryTaskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DeliveryPerson createDeliveryPerson(DeliveryPerson deliveryPerson) throws Exception {
        if (deliveryPersonRepository.existsByEmail(deliveryPerson.getEmail())) {
            throw new Exception("Delivery person already exists with this email");
        }

        deliveryPerson.setPassword(passwordEncoder.encode(deliveryPerson.getPassword()));
        deliveryPerson.setRole(USER_ROLE.ROLE_DELIVERY);
        deliveryPerson.setStatus(DeliveryStatus.PENDING);
        deliveryPerson.setEmailVerified(false);

        return deliveryPersonRepository.save(deliveryPerson);
    }

    @Override
    public DeliveryPerson getDeliveryPersonById(Long id) throws Exception {
        return deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new Exception("Delivery person not found with id: " + id));
    }

    @Override
    public DeliveryPerson getDeliveryPersonByEmail(String email) throws Exception {
        return deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Delivery person not found with email: " + email));
    }

    @Override
    public List<DeliveryPerson> getAvailableDeliveryPersons() {
        return deliveryPersonRepository.findByIsActiveTrue();
    }

    @Override
    public DeliveryPerson updateDeliveryPerson(Long id, DeliveryPerson deliveryPerson) throws Exception {
        DeliveryPerson existingDeliveryPerson = getDeliveryPersonById(id);
        existingDeliveryPerson.setFullName(deliveryPerson.getFullName());
        existingDeliveryPerson.setMobile(deliveryPerson.getMobile());
        existingDeliveryPerson.setLicenseNumber(deliveryPerson.getLicenseNumber());
        existingDeliveryPerson.setVehicleDetails(deliveryPerson.getVehicleDetails());
        existingDeliveryPerson.setCurrentLocation(deliveryPerson.getCurrentLocation());
        return deliveryPersonRepository.save(existingDeliveryPerson);
    }

    @Override
    public void deleteDeliveryPerson(Long id) throws Exception {
        DeliveryPerson deliveryPerson = getDeliveryPersonById(id);
        deliveryPersonRepository.delete(deliveryPerson);
    }

    @Override
    public DeliveryTask assignPickupTask(SupplyOrder supplyOrder) {
        DeliveryPerson availableDriver = findNearestAvailableDriver(supplyOrder.getSupplier().getPickupAddress());
        
        DeliveryTask task = new DeliveryTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setType(TaskType.PICKUP_FROM_SUPPLIER);
        task.setDeliveryPerson(availableDriver);
        task.setSupplyOrder(supplyOrder);
        task.setPickupAddress(supplyOrder.getSupplier().getPickupAddress());
        task.setStatus(TaskStatus.ASSIGNED);
        task.setAssignedDate(LocalDateTime.now());
        
        return deliveryTaskRepository.save(task);
    }

    @Override
    public DeliveryTask assignDeliveryTask(Order customerOrder) {
        DeliveryPerson availableDriver = findNearestAvailableDriver(customerOrder.getShippingAddress());
        
        DeliveryTask task = new DeliveryTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setType(TaskType.DELIVER_TO_CUSTOMER);
        task.setDeliveryPerson(availableDriver);
        task.setCustomerOrder(customerOrder);
        task.setDeliveryAddress(customerOrder.getShippingAddress());
        task.setStatus(TaskStatus.ASSIGNED);
        task.setAssignedDate(LocalDateTime.now());
        
        return deliveryTaskRepository.save(task);
    }

    @Override
    public DeliveryTask updateTaskStatus(Long taskId, TaskStatus status) throws Exception {
        DeliveryTask task = deliveryTaskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Task not found with id: " + taskId));
        
        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedDate(LocalDateTime.now());
        }
        
        return deliveryTaskRepository.save(task);
    }

    @Override
    public List<DeliveryTask> getTasksByDeliveryPerson(Long deliveryPersonId) throws Exception {
        DeliveryPerson deliveryPerson = getDeliveryPersonById(deliveryPersonId);
        return deliveryTaskRepository.findByDeliveryPerson(deliveryPerson);
    }

    @Override
    public List<DeliveryTask> getTasksByStatus(TaskStatus status) {
        return deliveryTaskRepository.findByStatus(status);
    }

    @Override
    public List<DeliveryTask> getTasksByType(TaskType type) {
        return deliveryTaskRepository.findByType(type);
    }

    @Override
    public DeliveryTask completeTask(Long taskId, String notes) throws Exception {
        DeliveryTask task = deliveryTaskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Task not found with id: " + taskId));
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedDate(LocalDateTime.now());
        task.setNotes(notes);
        
        return deliveryTaskRepository.save(task);
    }

    private DeliveryPerson findNearestAvailableDriver(com.camoutech.multivendor.model.Address address) {
        // Simple implementation - in real scenario, you would calculate distance
        List<DeliveryPerson> availableDrivers = deliveryPersonRepository.findByIsActiveTrue();
        return availableDrivers.isEmpty() ? null : availableDrivers.get(0);
    }
}
