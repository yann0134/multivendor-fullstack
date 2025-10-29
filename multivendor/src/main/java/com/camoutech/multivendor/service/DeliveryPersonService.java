package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.DeliveryPerson;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des livreurs
 */
public interface DeliveryPersonService {
    
    /**
     * Créer un nouveau livreur
     */
    DeliveryPerson createDeliveryPerson(DeliveryPerson deliveryPerson);
    
    /**
     * Récupérer un livreur par ID
     */
    DeliveryPerson getDeliveryPersonById(Long id);
    
    /**
     * Récupérer tous les livreurs actifs
     */
    List<DeliveryPerson> getAllActiveDeliveryPersons();
    
    /**
     * Récupérer tous les livreurs
     */
    List<DeliveryPerson> getAllDeliveryPersons();
    
    /**
     * Mettre à jour un livreur
     */
    DeliveryPerson updateDeliveryPerson(Long id, DeliveryPerson deliveryPerson);
    
    /**
     * Supprimer un livreur
     */
    void deleteDeliveryPerson(Long id);
    
    /**
     * Rechercher des livreurs par nom
     */
    List<DeliveryPerson> searchDeliveryPersonsByName(String name);
    
    /**
     * Récupérer un livreur par email
     */
    Optional<DeliveryPerson> getDeliveryPersonByEmail(String email);
    
    /**
     * Récupérer un livreur par téléphone
     */
    Optional<DeliveryPerson> getDeliveryPersonByPhone(String phone);
    
    /**
     * Vérifier si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Vérifier si un téléphone existe déjà
     */
    boolean existsByPhone(String phone);
}