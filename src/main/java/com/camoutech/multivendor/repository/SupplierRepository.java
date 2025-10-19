/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.AccountStatus;
import com.camoutech.multivendor.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findByEmail(String email);
    
    List<Supplier> findByAccountStatus(AccountStatus status);
    
    List<Supplier> findByIsEmailVerified(boolean isEmailVerified);
    
    boolean existsByEmail(String email);
}
