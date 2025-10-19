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
import com.camoutech.multivendor.repository.CartRepository;
import com.camoutech.multivendor.repository.SellerRepository;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.repository.VerificationCodeRepository;
import com.camoutech.multivendor.request.LoginRequest;
import com.camoutech.multivendor.response.AuthResponse;
import com.camoutech.multivendor.response.SignupRequest;
import com.camoutech.multivendor.service.AuthService;
import com.camoutech.multivendor.service.impl.CustomUserServiceImpl;
import com.camoutech.multivendor.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX="signin_";
        String SELLER_PREFIX = "seller_";

        if(email.startsWith(SIGNING_PREFIX)){
            email = email.substring(SIGNING_PREFIX.length());
        }
        
        if(email.startsWith(SELLER_PREFIX)){
            email = email.substring(SELLER_PREFIX.length());

            if (role.equals(USER_ROLE.ROLE_SELLER)){
                // Pour l'inscription, on permet l'envoi d'OTP même si le vendeur n'existe pas encore
                // Pour la connexion, on vérifie que le vendeur existe
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    // Si c'est pour l'inscription, on continue
                    // Si c'est pour la connexion, on lance une erreur
                    // Pour l'instant, on permet toujours l'envoi d'OTP
                    System.out.println("Vendeur non trouvé, mais envoi d'OTP autorisé pour l'inscription");
                }
            }
            else {
                User user = userRepository.findByEmail(email);
                if (user == null){
                    throw new Exception("user not exist with provided email");
                }
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
        verificationCodeRepository.save(verificationCode);

        String subject = "camoutech login/signup otp";
        String text = "your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email,otp,subject,text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {
        String email = req.getEmail();
        String SELLER_PREFIX = "seller_";
        boolean isSeller = false;
        
        // Vérifier si c'est un vendeur qui s'inscrit
        if(email.startsWith(SELLER_PREFIX)){
            email = email.substring(SELLER_PREFIX.length());
            isSeller = true;
        }

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
                seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION); // Le vendeur doit être approuvé
                
                seller = sellerRepository.save(seller);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_SELLER.toString()));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtProvider.generateToken(authentication);
        } else {
            // Créer un utilisateur client
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
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        String SELLER_PREFIX="seller_";
        if (username.startsWith(SELLER_PREFIX)){
            username = username.substring(SELLER_PREFIX.length());
        }

        if (userDetails == null){
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
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
