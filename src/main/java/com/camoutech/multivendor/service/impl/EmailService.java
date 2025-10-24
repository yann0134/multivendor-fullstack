/**
 * Created by camoutech
 * Date :15/10/2024
 * Time :14:54
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender javaMailSender;
    
    @Value("${spring.mail.enabled:true}")
    private boolean emailEnabled;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) throws MessagingException {
        
        // Mode développement : afficher l'OTP dans les logs
        if (!emailEnabled || javaMailSender == null) {
            logger.info("=== MODE DÉVELOPPEMENT - EMAIL DÉSACTIVÉ ===");
            logger.info("📧 Email destinataire: {}", userEmail);
            logger.info("📝 Sujet: {}", subject);
            logger.info("🔐 Code OTP: {}", otp);
            logger.info("📄 Contenu: {}", text);
            logger.info("==========================================");
            return;
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mimeMessageHelper.setTo(userEmail);
            javaMailSender.send(mimeMessage);
            
            logger.info("✅ Email OTP envoyé avec succès à: {}", userEmail);
        } catch (MailException e) {
            logger.error("❌ Erreur lors de l'envoi de l'email: {}", e.getMessage());
            throw new MailSendException("failed to send email: " + e.getMessage());
        }
    }
}
