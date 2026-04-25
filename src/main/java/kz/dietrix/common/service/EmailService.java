package kz.dietrix.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendResetCodeEmail(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Dietrix — Verification Code");
            helper.setText(buildCodeEmailHtml(userName, code), true);

            mailSender.send(message);
            log.info("Reset code email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildCodeEmailHtml(String userName, String code) {
        // Split code into individual digits for styling (smaller boxes)
        String digitBoxStyle = "display:inline-block;width:32px;height:40px;line-height:40px;" +
                "text-align:center;background:#f0f9f0;border:1px solid #4CAF50;" +
                "border-radius:6px;font-size:18px;font-weight:600;color:#2e7d32;margin:0 3px;";
        String digits = String.join("</span><span style=\"" + digitBoxStyle + "\">", code.split(""));

        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; color: #333;">
                    <div style="text-align: center; margin-bottom: 24px;">
                        <div style="display:inline-flex;align-items:center;justify-content:center;
                                    gap:8px;font-size:28px;font-weight:800;font-family:'Inter',Arial,sans-serif;">
                            <span style="color:#2d6a4f;background:white;padding:8px;border-radius:16px;
                                         box-shadow:0 4px 6px -1px rgba(0,0,0,0.10);font-size:24px;line-height:1;">
                                🌿
                            </span>
                            <span style="background:linear-gradient(145deg,#1e4d3a,#2d6a4f);
                                         -webkit-background-clip:text;-webkit-text-fill-color:transparent;
                                         background-clip:text;color:#2d6a4f;">
                                DIETRIX
                            </span>
                        </div>
                    </div>
                    <h2>Password Reset</h2>
                    <p>Hi, <strong>%s</strong>!</p>
                    <p>Enter this code in the app to reset your password:</p>
                    <div style="text-align: center; margin: 32px 0;">
                        <span style="%s">%s</span>
                    </div>
                    <p style="text-align: center; color: #888; font-size: 14px;">⏰ The code is valid for <strong>15 minutes</strong></p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;">
                    <p style="color: #aaa; font-size: 12px;">
                        If you did not request a password reset, please ignore this email.
                    </p>
                </body>
                </html>
                """.formatted(userName, digitBoxStyle, digits);
    }
}
