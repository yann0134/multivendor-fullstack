package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.repository.DeliveryPersonRepository;
import com.camoutech.multivendor.service.DeliveryPersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des livreurs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    @Override
    public DeliveryPerson createDeliveryPerson(DeliveryPerson deliveryPerson) {
        log.info("Création d'un nouveau livreur: {}", deliveryPerson.getName());
        
        // Vérifier si l'email existe déjà
        if (existsByEmail(deliveryPerson.getEmail())) {
            throw new RuntimeException("Un livreur avec cet email existe déjà");
        }
        
        // Vérifier si le téléphone existe déjà
        if (existsByPhone(deliveryPerson.getMobile())) {
            throw new RuntimeException("Un livreur avec ce téléphone existe déjà");
        }
        
        DeliveryPerson savedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("✅ Livreur créé avec l'ID: {}", savedDeliveryPerson.getId());
        
        return savedDeliveryPerson;
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryPerson getDeliveryPersonById(Long id) {
        log.info("Récupération du livreur avec l'ID: {}", id);
        
        return deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPerson> getAllActiveDeliveryPersons() {
        log.info("Récupération de tous les livreurs actifs");
        
        List<DeliveryPerson> activeDeliveryPersons = deliveryPersonRepository.findByIsActiveTrue();
        log.info("✅ {} livreurs actifs récupérés", activeDeliveryPersons.size());
        
        return activeDeliveryPersons;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPerson> getAllDeliveryPersons() {
        log.info("Récupération de tous les livreurs");
        
        List<DeliveryPerson> allDeliveryPersons = deliveryPersonRepository.findAll();
        log.info("✅ {} livreurs récupérés", allDeliveryPersons.size());
        
        return allDeliveryPersons;
    }

    @Override
    public DeliveryPerson updateDeliveryPerson(Long id, DeliveryPerson deliveryPerson) {
        log.info("Mise à jour du livreur avec l'ID: {}", id);
        
        DeliveryPerson existingDeliveryPerson = getDeliveryPersonById(id);
        
        // Vérifier si l'email existe déjà (sauf pour le livreur actuel)
        if (!existingDeliveryPerson.getEmail().equals(deliveryPerson.getEmail()) && 
            existsByEmail(deliveryPerson.getEmail())) {
            throw new RuntimeException("Un livreur avec cet email existe déjà");
        }
        
        // Vérifier si le téléphone existe déjà (sauf pour le livreur actuel)
        if (!existingDeliveryPerson.getMobile().equals(deliveryPerson.getMobile()) && 
            existsByPhone(deliveryPerson.getMobile())) {
            throw new RuntimeException("Un livreur avec ce téléphone existe déjà");
        }
        
        // Mettre à jour les propriétés
        existingDeliveryPerson.setFullName(deliveryPerson.getFullName());
        existingDeliveryPerson.setEmail(deliveryPerson.getEmail());
        existingDeliveryPerson.setMobile(deliveryPerson.getMobile());
        existingDeliveryPerson.setIsActive(deliveryPerson.getIsActive());
        existingDeliveryPerson.setVehicleType(deliveryPerson.getVehicleType());
        existingDeliveryPerson.setLicenseNumber(deliveryPerson.getLicenseNumber());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(existingDeliveryPerson);
        log.info("✅ Livreur mis à jour avec l'ID: {}", updatedDeliveryPerson.getId());
        
        return updatedDeliveryPerson;
    }

    @Override
    public void deleteDeliveryPerson(Long id) {
        log.info("Suppression du livreur avec l'ID: {}", id);
        
        DeliveryPerson deliveryPerson = getDeliveryPersonById(id);
        deliveryPersonRepository.delete(deliveryPerson);
        
        log.info("✅ Livreur supprimé avec l'ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPerson> searchDeliveryPersonsByName(String name) {
        log.info("Recherche de livreurs par nom: {}", name);
        
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findByFullNameContainingIgnoreCase(name);
        log.info("✅ {} livreurs trouvés pour la recherche: {}", deliveryPersons.size(), name);
        
        return deliveryPersons;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryPerson> getDeliveryPersonByEmail(String email) {
        log.info("Récupération du livreur par email: {}", email);
        
        return deliveryPersonRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryPerson> getDeliveryPersonByPhone(String phone) {
        log.info("Récupération du livreur par téléphone: {}", phone);
        
        return deliveryPersonRepository.findByMobile(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return deliveryPersonRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return deliveryPersonRepository.existsByMobile(phone);
    }
}
