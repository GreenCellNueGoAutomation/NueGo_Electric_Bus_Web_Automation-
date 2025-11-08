package utils;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailUtils {

    /**
     * Send an email with optional attachment.
     *
     * @param toEmail        Recipient email address
     * @param subject        Email subject
     * @param body           Email body text
     * @param attachmentPath Full path to attachment file (optional, can be null)
     */
    public static void sendEmail(String toEmail, String subject, String body, String attachmentPath) {
    	final String fromEmail = "sumedh.sonawane@sumasoft.net"; // your email
    	final String password = "oxzrysredrqsebmg"; // Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // TLS port

        // Create session
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            // Attachment part (optional)
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(Paths.get(attachmentPath).toFile());
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(Paths.get(attachmentPath).getFileName().toString());
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    // Optional: Simple test main
    public static void main(String[] args) {
        sendEmail(
                "sumedhsonwane19@gmail.com",
                "Test Email",
                "Hello, this is a test email with attachment!",
                "C:/path/to/file.txt"
        );
    }
}
