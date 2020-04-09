package Advisor.Mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {
    public static void sendMail(String recipient,String myMessage){
        System.out.println("Sending");
        Properties p = new Properties();
        p.put("mail.smtp.auth","true");
        p.put("mail.smtp.starttls.enable","true");
        p.put("mail.smtp.host","smtp.gmail.com");
        p.put("mail.smtp.port","587");

        final String myAccount = "ashwinathappank@gmail.com";
        final String myPassword = "IamAshwin@Brownie02";

        Session session = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccount,myPassword);
            }
        });

        Message message = prepareMessage(myMessage,session,myAccount,recipient);

        try {
            assert message != null;
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Sent");
    }

    public static Message prepareMessage (String myMessage, Session session, String email, String rec){
        try {
            Message m = new MimeMessage(session);
            m.setFrom(new InternetAddress(email));
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            m.setSubject("IT ADVISOR");
            m.setText(myMessage);
            return m;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
