package com.example.NAGOYAMESHI.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.service.PasswordResetTokenService;

@Component
public class PasswordResetEventListener {
    private final PasswordResetTokenService passwordResetTokenService;
    private final JavaMailSender javaMailSender;

    public PasswordResetEventListener(PasswordResetTokenService passwordResetTokenService, JavaMailSender mailSender) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.javaMailSender = mailSender;
    }

    @EventListener
    public void onPasswordResetEvent(PasswordResetEvent passwordResetEvent) {
        User user = passwordResetEvent.getUser();
        String token = UUID.randomUUID().toString();

        // トークン作成前に既存のトークンを削除
        passwordResetTokenService.deleteByUser(user);
        passwordResetTokenService.create(user, token);

        // パスワード再設定ページのURLを生成
        String resetPasswordUrl = passwordResetEvent.getRequestUrl().replace("/request", "/reset-password/form?token=") + token;
        String subject = "パスワード再設定のご案内";
        String message = "パスワード再設定フォームにアクセスするには、以下のURLをクリックしてください。\n" + resetPasswordUrl;
        String senderAddress = "momenn54@gmail.com"; // 必要に応じて設定
        String recipientAddress = user.getEmail();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderAddress);
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }
}