/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.config.JwtProvider;
import com.camoutech.multivendor.domain.AccountStatus;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.exceptions.SupplierException;
import com.camoutech.multivendor.model.Address;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.repository.AddressRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Override
    public Supplier getSupplierProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSupplierByEmail(email);
    }

    @Override
    public Supplier createSupplier(Supplier supplier) throws Exception {
        Supplier supplierExist = supplierRepository.findByEmail(supplier.getEmail()).orElse(null);
        if (supplierExist != null) {
            throw new Exception("supplier already exist, used different email");
        }

        Address savedAddress = addressRepository.save(supplier.getPickupAddress());

        Supplier newSupplier = new Supplier();
        newSupplier.setEmail(supplier.getEmail());
        newSupplier.setPassword(passwordEncoder.encode(supplier.getPassword()));
        newSupplier.setSupplierName(supplier.getSupplierName());
        newSupplier.setPickupAddress(savedAddress);
        newSupplier.setGSTIN(supplier.getGSTIN());
        newSupplier.setRole(USER_ROLE.ROLE_SUPPLIER);
        newSupplier.setMobile(supplier.getMobile());
        newSupplier.setBankDetails(supplier.getBankDetails());
        newSupplier.setBusinessDetails(supplier.getBusinessDetails());
        return supplierRepository.save(newSupplier);
    }

    @Override
    public Supplier getSupplierById(Long id) throws SupplierException {
        return supplierRepository.findById(id).orElseThrow(() -> new SupplierException("supplier not found with id" + id));
    }

    @Override
    public Supplier getSupplierByEmail(String email) throws Exception {
        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        if (supplier == null) {
            throw new Exception("Supplier not found ....");
        }
        return supplier;
    }

    @Override
    public List<Supplier> getAllSuppliers(AccountStatus status) {
        return supplierRepository.findByAccountStatus(status);
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplier) throws Exception {
        Supplier existingSupplier = getSupplierById(id);
        existingSupplier.setSupplierName(supplier.getSupplierName());
        existingSupplier.setMobile(supplier.getMobile());
        existingSupplier.setBusinessDetails(supplier.getBusinessDetails());
        existingSupplier.setBankDetails(supplier.getBankDetails());
        return supplierRepository.save(existingSupplier);
    }

    @Override
    public void deleteSupplier(Long id) throws Exception {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    @Override
    public Supplier verifyEmail(String email, String otp) throws Exception {
        // Implementation for email verification
        Supplier supplier = getSupplierByEmail(email);
        supplier.setEmailVerified(true);
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplierAccountStatus(Long supplierId, AccountStatus status) throws Exception {
        Supplier supplier = getSupplierById(supplierId);
        supplier.setAccountStatus(status);
        return supplierRepository.save(supplier);
    }
}
