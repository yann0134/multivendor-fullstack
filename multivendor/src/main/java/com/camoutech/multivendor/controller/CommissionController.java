/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.OrderItem;
import com.camoutech.multivendor.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commission")
public class CommissionController {

    private final CommissionService commissionService;

    @GetMapping("/supplier/{supplierId}/payment")
    public ResponseEntity<Double> getSupplierPayment(@PathVariable Long supplierId,
                                                     @RequestParam LocalDateTime from,
                                                     @RequestParam LocalDateTime to) {
        double payment = commissionService.calculateSupplierPayment(supplierId, from, to);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/supplier/{supplierId}/earnings")
    public ResponseEntity<Double> getSupplierEarnings(@PathVariable Long supplierId,
                                                      @RequestParam LocalDateTime from,
                                                      @RequestParam LocalDateTime to) {
        double earnings = commissionService.getSupplierEarnings(supplierId, from, to);
        return new ResponseEntity<>(earnings, HttpStatus.OK);
    }

    @GetMapping("/supermarket/total")
    public ResponseEntity<Double> getTotalCommissionEarned(@RequestParam LocalDateTime from,
                                                           @RequestParam LocalDateTime to) {
        double totalCommission = commissionService.getTotalCommissionEarned(from, to);
        return new ResponseEntity<>(totalCommission, HttpStatus.OK);
    }

    @GetMapping("/supplier/{supplierId}/sold-items")
    public ResponseEntity<List<OrderItem>> getSoldItemsBySupplier(@PathVariable Long supplierId,
                                                                   @RequestParam LocalDateTime from,
                                                                   @RequestParam LocalDateTime to) {
        List<OrderItem> soldItems = commissionService.getSoldItemsBySupplier(supplierId, from, to);
        return new ResponseEntity<>(soldItems, HttpStatus.OK);
    }
}
