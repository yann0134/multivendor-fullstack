/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.AccountStatus;
import com.camoutech.multivendor.exceptions.SupplierException;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/profile")
    public ResponseEntity<Supplier> getSupplierProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        Supplier supplier = supplierService.getSupplierProfile(jwt);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) throws Exception {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long supplierId) throws SupplierException {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Supplier>> getAllSuppliers(@RequestParam(required = false) AccountStatus status) {
        List<Supplier> suppliers = status != null ? 
            supplierService.getAllSuppliers(status) : 
            supplierService.getAllSuppliers(AccountStatus.ACTIVE);
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long supplierId, @RequestBody Supplier supplier) throws Exception {
        Supplier updatedSupplier = supplierService.updateSupplier(supplierId, supplier);
        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long supplierId) throws Exception {
        supplierService.deleteSupplier(supplierId);
        return new ResponseEntity<>("Supplier deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Supplier> verifyEmail(@RequestParam String email, @RequestParam String otp) throws Exception {
        Supplier supplier = supplierService.verifyEmail(email, otp);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PutMapping("/{supplierId}/status")
    public ResponseEntity<Supplier> updateSupplierStatus(@PathVariable Long supplierId, @RequestParam AccountStatus status) throws Exception {
        Supplier supplier = supplierService.updateSupplierAccountStatus(supplierId, status);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }
}
