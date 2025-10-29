/**
 * Created by camoutech
 * Date :14/10/2024
 * Time :15:38
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.config.JwtProvider;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.domain.AccountStatus;
import com.camoutech.multivendor.model.Cart;
import com.camoutech.multivendor.model.Seller;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.model.VerificationCode;
import com.camoutech.multivendor.model.WarehouseUser;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.repository.CartRepository;
import com.camoutech.multivendor.repository.SellerRepository;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.repository.VerificationCodeRepository;
import com.camoutech.multivendor.repository.WarehouseUserRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.repository.DeliveryPersonRepository;
import com.camoutech.multivendor.request.LoginRequest;
import com.camoutech.multivendor.response.AuthResponse;
import com.camoutech.multivendor.response.SignupRequest;
import com.camoutech.multivendor.service.AuthService;
import com.camoutech.multivendor.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;
    private final WarehouseUserRepository warehouseUserRepository;
    private final SupplierRepository supplierRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
                String SIGNING_PREFIX="signin_";
                String SELLER_PREFIX = "seller_";
                String SUPPLIER_PREFIX = "supplier_";
                String WAREHOUSE_PREFIX = "warehouse_";
                String DELIVERY_PREFIX = "delivery_";
                String ADMIN_PREFIX = "admin_";
        
                if(email.startsWith(SIGNING_PREFIX)){
                    email = email.substring(SIGNING_PREFIX.length());
                }
                
                if(email.startsWith(SELLER_PREFIX)){
                    email = email.substring(SELLER_PREFIX.length());
                } else if(email.startsWith(SUPPLIER_PREFIX)){
                    email = email.substring(SUPPLIER_PREFIX.length());
                } else if(email.startsWith(WAREHOUSE_PREFIX)){
                    email = email.substring(WAREHOUSE_PREFIX.length());
                } else if(email.startsWith(DELIVERY_PREFIX)){
                    email = email.substring(DELIVERY_PREFIX.length());
                } else if(email.startsWith(ADMIN_PREFIX)){
                    email = email.substring(ADMIN_PREFIX.length());
                }
        
                if (role.equals(USER_ROLE.ROLE_SELLER)){
                    // Pour les vendeurs, on permet toujours l'envoi d'OTP (inscription ou connexion)
                    Seller seller = sellerRepository.findByEmail(email);
                    if (seller == null) {
                        System.out.println("Vendeur non trouvé - OTP envoyé pour inscription possible");
                    } else {
                        System.out.println("Vendeur trouvé - OTP envoyé pour connexion");
                    }
                }
                else {
                    User user = userRepository.findByEmail(email);
                    if (user == null){
                        System.out.println("Utilisateur non trouvé - OTP envoyé pour inscription possible");
                    }
                }

        VerificationCode isExist = verificationCodeRepository.findByEmail(email);
        if(isExist != null){
            verificationCodeRepository.delete(isExist);
        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        System.out.println("Saving verification code for email: " + email);
        verificationCodeRepository.save(verificationCode);

        String subject = "camoutech login/signup otp";
        String text = "your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email,otp,subject,text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {
        String email = req.getEmail();
        String SELLER_PREFIX = "seller_";
        String SUPPLIER_PREFIX = "supplier_";
        String WAREHOUSE_PREFIX = "warehouse_";
        String DELIVERY_PREFIX = "delivery_";
        String ADMIN_PREFIX = "admin_";
        
        boolean isSeller = false;
        boolean isSupplier = false;
        boolean isWarehouse = false;
        boolean isDelivery = false;
        boolean isAdmin = false;
        
        // Vérifier le type d'utilisateur qui s'inscrit
        if(email.startsWith(SELLER_PREFIX)){
            email = email.substring(SELLER_PREFIX.length());
            isSeller = true;
        } else if(email.startsWith(SUPPLIER_PREFIX)){
            email = email.substring(SUPPLIER_PREFIX.length());
            isSupplier = true;
        } else if(email.startsWith(WAREHOUSE_PREFIX)){
            email = email.substring(WAREHOUSE_PREFIX.length());
            isWarehouse = true;
        } else if(email.startsWith(DELIVERY_PREFIX)){
            email = email.substring(DELIVERY_PREFIX.length());
            isDelivery = true;
        } else if(email.startsWith(ADMIN_PREFIX)) {
            email = email.substring(ADMIN_PREFIX.length());
            isAdmin = true;
        }
        
                System.out.println("Finding verification code for email: " + email);
                VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        
                if (verificationCode == null) {
            throw new Exception("Aucun code de vérification trouvé pour cet email. Veuillez d'abord demander un code OTP.");
        }
        
        if (!verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Code OTP incorrect. Veuillez vérifier le code reçu par email.");
        }
        
        // Vérifier si le code OTP a expiré (par exemple, 10 minutes)
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpTime = verificationCode.getCreatedAt();
        long minutesDifference = java.time.Duration.between(otpTime, currentTime).toMinutes();
        
        if (minutesDifference > 10) {
            throw new Exception("Le code OTP a expiré. Veuillez demander un nouveau code.");
        }

        if (isSeller) {
            // Créer un vendeur
            Seller seller = sellerRepository.findByEmail(email);
            if (seller == null) {
                seller = new Seller();
                seller.setEmail(email);
                seller.setSellerName(req.getFullName());
                seller.setRole(USER_ROLE.ROLE_SELLER);
                seller.setMobile("778046375");
                seller.setPassword(passwordEncoder.encode(req.getOtp()));
                seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
                
                seller = sellerRepository.save(seller);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_SELLER.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else if (isSupplier) {
            // Créer un fournisseur
            Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
            if (supplier == null) {
                supplier = new Supplier();
                supplier.setEmail(email);
                supplier.setSupplierName(req.getFullName());
                supplier.setRole(USER_ROLE.ROLE_SUPPLIER);
                supplier.setMobile("778046375");
                supplier.setPassword(passwordEncoder.encode(req.getOtp()));
                supplier.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
                
                supplier = supplierRepository.save(supplier);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_SUPPLIER.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else if (isWarehouse) {
            // Créer un utilisateur entrepôt
            WarehouseUser warehouseUser = warehouseUserRepository.findByEmail(email).orElse(null);
            if (warehouseUser == null) {
                warehouseUser = new WarehouseUser();
                warehouseUser.setEmail(email);
                warehouseUser.setFullName(req.getFullName());
                warehouseUser.setRole(USER_ROLE.ROLE_WAREHOUSE);
                warehouseUser.setMobile("778046375");
                warehouseUser.setPassword(passwordEncoder.encode(req.getOtp()));
                warehouseUser.setWarehouseName("Entrepôt " + req.getFullName());
                warehouseUser.setWarehouseLocation("Douala, Cameroun");
                warehouseUser.setWarehouseCode("WH" + System.currentTimeMillis());
                warehouseUser.setAccountStatus("ACTIVE");
                
                warehouseUser = warehouseUserRepository.save(warehouseUser);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_WAREHOUSE.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else if (isDelivery) {
            // Créer un agent/livreur
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findByEmail(email).orElse(null);
            if (deliveryPerson == null) {
                deliveryPerson = new DeliveryPerson();
                deliveryPerson.setEmail(email);
                deliveryPerson.setFullName(req.getFullName());
                deliveryPerson.setRole(USER_ROLE.ROLE_DELIVERY);
                deliveryPerson.setMobile("778046375");
                deliveryPerson.setPassword(passwordEncoder.encode(req.getOtp()));
                
                deliveryPerson = deliveryPersonRepository.save(deliveryPerson);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_DELIVERY.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else if (isAdmin) {
            // Créer un administrateur
            User admin = userRepository.findByEmail(email);
            if (admin == null) {
                admin = new User();
                admin.setEmail(email);
                admin.setFullName(req.getFullName());
                admin.setRole(USER_ROLE.ROLE_ADMIN);
                admin.setMobile("778046375");
                admin.setPassword(passwordEncoder.encode(req.getOtp()));
                
                admin = userRepository.save(admin);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_ADMIN.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else {
            // Créer un utilisateur client par défaut
            User user = userRepository.findByEmail(email);

        if (user==null){
            User createdUser = new User();
                createdUser.setEmail(email);
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("778046375");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
        }
    }

    @Override
    public AuthResponse signin(LoginRequest req) throws Exception {
        String username = req.getEmail();
        String otp = req.getOtp();
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws Exception {
        String SELLER_PREFIX="seller_";
        boolean isSeller = false;
        if (username.startsWith(SELLER_PREFIX)){
            username = username.substring(SELLER_PREFIX.length());
            isSeller = true;
        }

        UserDetails userDetails = null;
        try {
            userDetails = customUserService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            // Si c'est un vendeur qui n'existe pas encore, on continue avec la vérification OTP
            if (isSeller) {
                System.out.println("Vendeur non trouvé - vérification OTP pour inscription");
            } else {
                throw new BadCredentialsException("invalid username or password");
            }
        }

        if (userDetails == null && !isSeller){
            throw new BadCredentialsException("invalid username or password");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null) {
            throw new Exception("Aucun code de vérification trouvé pour cet email. Veuillez d'abord demander un code OTP.");
        }
        
        if (!verificationCode.getOtp().equals(otp)) {
            throw new Exception("Code OTP incorrect. Veuillez vérifier le code reçu par email.");
        }
        
        // Vérifier si le code OTP a expiré (par exemple, 10 minutes)
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpTime = verificationCode.getCreatedAt();
        long minutesDifference = java.time.Duration.between(otpTime, currentTime).toMinutes();
        
        if (minutesDifference > 10) {
            throw new Exception("Le code OTP a expiré. Veuillez demander un nouveau code.");
        }
        
        // Si c'est un vendeur qui n'existe pas encore, créer des autorités temporaires
        if (userDetails == null && isSeller) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_SELLER.toString()));
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        }
        
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
