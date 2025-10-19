/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    
    Optional<DeliveryPerson> findByEmail(String email);
    
    List<DeliveryPerson> findByStatus(DeliveryStatus status);
    
    List<DeliveryPerson> findByIsEmailVerified(boolean isEmailVerified);
    
    boolean existsByEmail(String email);
}
