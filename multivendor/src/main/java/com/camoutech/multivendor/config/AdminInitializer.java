/**
 * Created by camoutech
 * Project Name : multivendor
 */

package com.camoutech.multivendor.config;

import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@agrimarket.local}")
    private String adminEmail;

    @Value("${app.admin.password:ChangeMe123!}")
    private String adminPassword;

    @Value("${app.admin.full-name:Super Admin}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        // Vérifier par email en priorité
        User existingByEmail = userRepository.findByEmail(adminEmail);
        if (existingByEmail != null) {
            return; // déjà présent
        }

        // Sinon, vérifier s'il existe déjà un admin (sécurité)
        boolean anyAdminExists = userRepository.findAll().stream()
                .anyMatch(u -> USER_ROLE.ROLE_ADMIN.equals(u.getRole()));
        if (anyAdminExists) {
            return;
        }

        // Créer l'admin par défaut
        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setFullName(adminFullName);
        admin.setRole(USER_ROLE.ROLE_ADMIN);
        admin.setMobile("000000000");
        admin.setPassword(passwordEncoder.encode(adminPassword));

        userRepository.save(admin);
        System.out.println("⚙️ Administrateur par défaut créé: " + adminEmail);
    }
}


