package utils;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;

public class EmailUtils {

    /**
     * Send an email with optional attachments to multiple recipients.
     *
     * @param toEmails       List of recipient email addresses
     * @param subject        Email subject
     * @param body           Email body text
     * @param attachments    List of file paths to attach (can be empty)
     */
    public static void sendEmail(List<String> toEmails, String subject, String body, List<String> attachments) {
        final String fromEmail = "sumedh.sonawane@sumasoft.net"; // your email
        final String password = "oxzrysredrqsebmg"; // Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // TLS port

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));

            InternetAddress[] recipientAddresses = toEmails.stream()
                    .map(email -> {
                        try { return new InternetAddress(email); }
                        catch (AddressException e) { e.printStackTrace(); return null; }
                    })
                    .filter(addr -> addr != null)
                    .toArray(InternetAddress[]::new);

            message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            message.setSubject(subject);

            // Multipart
            Multipart multipart = new MimeMultipart();

            // Body part
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);
            multipart.addBodyPart(bodyPart);

            // Attachments
            if (attachments != null) {
                for (String path : attachments) {
                    if (path != null && !path.isEmpty()) {
                        MimeBodyPart attachPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(Paths.get(path).toFile());
                        attachPart.setDataHandler(new DataHandler(source));
                        attachPart.setFileName(Paths.get(path).getFileName().toString());
                        multipart.addBodyPart(attachPart);
                    }
                }
            }

            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("✅ Email sent successfully to: " + String.join(", ", toEmails));

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
