#Mail on Java

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import javafx.concurrent.Task;

public class Mail extends Task<String> {

    protected final Logger LOGGER = Logger.getLogger(Mail.class.getName());
    protected final String SMTP_HOST_NAME = "smtp.gmail.com";
    protected final int SMTP_HOST_PORT = 465;
    protected String message;
    
    public Mail(String message) {
        this.message = message;
    }
    
    public void sendMail(String From, String FromPwd, String To, String subject, String msg) throws NoSuchProviderException, MessagingException {

        System.setProperty("https.protocols", "TLSv1.1");
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        MimeMessage mimeMessage = new MimeMessage(mailSession);
        mimeMessage.setSubject(subject);
        mimeMessage.setText(msg);

        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
        mimeMessage.setReplyTo(null);

        transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, From, FromPwd);
        transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /* check internet connection */
    private boolean hasConnection() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String call() throws SocketException {
        if (hasConnection()) {
            try {
                sendMail("FROM_EMAIL", "FROM_PASSWORD", "TO_EMAIL", "SUBJECT", "MESSAGE");
            } catch (NoSuchProviderException ex) {
                LOGGER.error(ex);
                return "Error " + ex;
            } catch (MessagingException ex) {
                LOGGER.error(ex);
                return "Error " + ex;
            }
            return "Thanks for your message!";
        } else {
            return "No internet connection";
        }
    }
