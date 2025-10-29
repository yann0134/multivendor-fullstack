package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des livreurs
 */
@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    
    /**
     * Récupérer tous les livreurs actifs
     */
    List<DeliveryPerson> findByIsActiveTrue();
    
    /**
     * Récupérer les livreurs par nom
     */
    List<DeliveryPerson> findByFullNameContainingIgnoreCase(String name);
    
    /**
     * Récupérer les livreurs par email
     */
    Optional<DeliveryPerson> findByEmail(String email);
    
    /**
     * Récupérer les livreurs par téléphone
     */
    Optional<DeliveryPerson> findByMobile(String mobile);
    
    /**
     * Vérifier si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifier si un téléphone existe déjà
     */
    boolean existsByMobile(String mobile);
}