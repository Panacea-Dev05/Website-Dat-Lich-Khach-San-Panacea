package panacea.website_dat_lich_khach_san.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    /**
     * Gửi email đơn giản với text content
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Email not sent.");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email", e);
        }
    }
    
    /**
     * Gửi email với HTML content
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Email not sent.");
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Lỗi gửi email HTML: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email HTML", e);
        }
    }
}