/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.domain.TaskType;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.model.DeliveryTask;
import com.camoutech.multivendor.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/person/create")
    public ResponseEntity<DeliveryPerson> createDeliveryPerson(@RequestBody DeliveryPerson deliveryPerson) throws Exception {
        DeliveryPerson createdDeliveryPerson = deliveryService.createDeliveryPerson(deliveryPerson);
        return new ResponseEntity<>(createdDeliveryPerson, HttpStatus.CREATED);
    }

    @GetMapping("/person/{deliveryPersonId}")
    public ResponseEntity<DeliveryPerson> getDeliveryPersonById(@PathVariable Long deliveryPersonId) throws Exception {
        DeliveryPerson deliveryPerson = deliveryService.getDeliveryPersonById(deliveryPersonId);
        return new ResponseEntity<>(deliveryPerson, HttpStatus.OK);
    }

    @GetMapping("/person/available")
    public ResponseEntity<List<DeliveryPerson>> getAvailableDeliveryPersons() {
        List<DeliveryPerson> deliveryPersons = deliveryService.getAvailableDeliveryPersons();
        return new ResponseEntity<>(deliveryPersons, HttpStatus.OK);
    }

    @PutMapping("/person/{deliveryPersonId}")
    public ResponseEntity<DeliveryPerson> updateDeliveryPerson(@PathVariable Long deliveryPersonId, @RequestBody DeliveryPerson deliveryPerson) throws Exception {
        DeliveryPerson updatedDeliveryPerson = deliveryService.updateDeliveryPerson(deliveryPersonId, deliveryPerson);
        return new ResponseEntity<>(updatedDeliveryPerson, HttpStatus.OK);
    }

    @DeleteMapping("/person/{deliveryPersonId}")
    public ResponseEntity<String> deleteDeliveryPerson(@PathVariable Long deliveryPersonId) throws Exception {
        deliveryService.deleteDeliveryPerson(deliveryPersonId);
        return new ResponseEntity<>("Delivery person deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<DeliveryTask>> getTasks(@RequestParam(required = false) Long deliveryPersonId,
                                                       @RequestParam(required = false) TaskStatus status,
                                                       @RequestParam(required = false) TaskType type) throws Exception {
        List<DeliveryTask> tasks;
        
        if (deliveryPersonId != null) {
            tasks = deliveryService.getTasksByDeliveryPerson(deliveryPersonId);
        } else if (status != null) {
            tasks = deliveryService.getTasksByStatus(status);
        } else if (type != null) {
            tasks = deliveryService.getTasksByType(type);
        } else {
            tasks = List.of(); // Return empty list if no filters
        }
        
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PutMapping("/task/{taskId}/status")
    public ResponseEntity<DeliveryTask> updateTaskStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) throws Exception {
        DeliveryTask task = deliveryService.updateTaskStatus(taskId, status);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @PutMapping("/task/{taskId}/complete")
    public ResponseEntity<DeliveryTask> completeTask(@PathVariable Long taskId, @RequestParam(required = false) String notes) throws Exception {
        DeliveryTask task = deliveryService.completeTask(taskId, notes);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }
}
