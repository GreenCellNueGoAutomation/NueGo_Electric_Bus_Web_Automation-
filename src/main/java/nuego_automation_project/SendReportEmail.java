package nuego_automation_project;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class SendReportEmail {

    public static void main(String[] args) {
        try {
            // ✅ Recipients and sender
            String to = "sumedh.sonawane@sumasoft.net" ;
          //  String cc = "";
                         
            String from = "sumedh.sonawane@sumasoft.net";

            // ⚠️ Gmail App Password (use valid app password)
            String password = "jtkurhehuqlmmduo";

            // ✅ Get build number (from Jenkins or local)
            String buildNum = System.getProperty("buildNum", "local");

            // ✅ ExtentReports folder
            File reportDir = new File(System.getProperty("user.dir")
                    + File.separator + "target"
                    + File.separator + "ExtentReports");

            if (!reportDir.exists() || !reportDir.isDirectory()) {
                throw new Exception("❌ ExtentReports directory not found at: " + reportDir.getAbsolutePath());
            }

            // ✅ Find latest HTML report file
            File[] reports = reportDir.listFiles((dir, name) -> name.endsWith(".html"));
            if (reports == null || reports.length == 0) {
                throw new Exception("❌ No HTML reports found in: " + reportDir.getAbsolutePath());
            }

            File latestReport = reports[0];
            for (File report : reports) {
                if (report.lastModified() > latestReport.lastModified()) {
                    latestReport = report;
                }
            }

            System.out.println("✅ Found latest report: " + latestReport.getName());

            // ✅ Gmail SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            // ✅ Create session
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            // ✅ Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
          //  message.setRecipients(Message.RecipientType.CC,
           //         InternetAddress.parse(cc));
            message.setSubject("NueGo Automation Test Report - Build " + buildNum);

            // ✅ Message body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                "Hi Team,\n\n" +
                "Please find attached the latest automation test report.\n\n" +
                "Report Name: " + latestReport.getName() + "\n\n" +
                "Thanks & Regards,\n" +
                "Sumedh Sonawane\n" +
                "Quality Assurance Engineer\n" +
                "SumaSoft Pvt. Ltd.\n" +
                "Aundh | Pune"
            );

            // ✅ Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(latestReport);

            // ✅ Combine and send
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email sent successfully with report: " + latestReport.getName());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
} 
