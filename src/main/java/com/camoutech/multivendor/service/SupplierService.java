package com.camoutech.multivendor.service;

import com.camoutech.multivendor.domain.AccountStatus;
import com.camoutech.multivendor.exceptions.SupplierException;
import com.camoutech.multivendor.model.Supplier;

import java.util.List;

public interface SupplierService {

    Supplier getSupplierProfile(String jwt) throws Exception;
    Supplier createSupplier(Supplier supplier) throws Exception;
    Supplier getSupplierById(Long id) throws SupplierException;
    Supplier getSupplierByEmail(String email) throws Exception;
    List<Supplier> getAllSuppliers(AccountStatus status);
    Supplier updateSupplier(Long id, Supplier supplier) throws Exception;
    void deleteSupplier(Long id) throws Exception;
    Supplier verifyEmail(String email, String otp) throws Exception;
    Supplier updateSupplierAccountStatus(Long supplierId, AccountStatus status) throws Exception;
}
