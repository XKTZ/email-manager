package xktz.mail.account;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import xktz.mail.MailException;
import xktz.mail.element.MailElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * IMAP & SMTP account connection. It is the basic account class can connect to the IMAP and SMTP server.
 *
 * @author XKTZ
 * @date 2022-06-13
 */
public class MailAccount {

    /**
     * Property IMAP
     */
    private static final Properties IMAP_PROPERTY = new Properties() {{
        setProperty("mail.store.protocol", "imaps");
    }};

    private static final Properties SMTP_PROPERTY = new Properties() {{
        setProperty("mail.smtp.auth", "true");
        setProperty("mail.smtp.starttls.enable", "true");
    }};

    /**
     * IMAP Protocol
     */
    private static final String IMAP_PROTOCOL = "imaps";

    /**
     * IMAP Server
     */
    private String imapServer;

    /**
     * IMAP port
     */
    private int imapPort;

    /**
     * SMTP server
     */
    private String smtpServer;

    /**
     * SMTP port
     */
    private int smtpPort;

    /**
     * Account
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * session to the server
     */
    private Session imapSession;

    /**
     * SMTP session
     */
    private Session smtpSession;

    /**
     * store
     */
    private Store imapStore;

    /**
     * Root
     */
    private MailFolder root;

    /**
     * IMAP account
     *
     * @param imapServer imapServer
     * @param username   username
     * @param password   password
     * @throws MailException
     */
    public MailAccount(String imapServer, int imapPort,
                       String smtpServer, int smtpPort,
                       String username, String password) {
        try {
            this.imapServer = imapServer;
            this.imapPort = imapPort;
            this.smtpServer = smtpServer;
            this.smtpPort = smtpPort;
            this.username = username;
            this.password = password;
            login();
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    /**
     * login
     *
     * @throws MessagingException
     */
    private void login() throws MessagingException {
        // set imap
        imapSession = Session.getInstance(IMAP_PROPERTY);
        imapStore = imapSession.getStore(IMAP_PROTOCOL);
        imapStore.connect(this.imapServer, this.imapPort, this.username, this.password);

        // set smtp
        Properties smtpProperty = (Properties) SMTP_PROPERTY.clone();
        smtpProperty.setProperty("mail.smtp.host", smtpServer);
        smtpProperty.setProperty("mail.smtp.port", Integer.toString(smtpPort));
        smtpSession = Session.getInstance(smtpProperty, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // refresh
        refresh();
    }

    /**
     * refresh
     *
     * @throws MessagingException
     */
    public void refresh() throws MessagingException {
        root = MailFolder.of(imapStore.getDefaultFolder());
    }

    /**
     * Get root
     *
     * @return root
     * @throws MessagingException
     */
    public MailFolder root() throws MessagingException {
        return root;
    }

    /**
     * Get a folder from path sequence
     *
     * @param paths path sequence
     * @return folder
     * @throws MessagingException
     */
    public MailFolder direct(String[] paths) throws MessagingException {
        return root.direct(paths);
    }

    /**
     * Send email with default address
     *
     * @param to      to
     * @param subject subject
     * @param root    mail element root
     * @throws MessagingException
     */
    public void sendMessage(String[] to,
                            String subject,
                            MailElement root) throws MessagingException {
        var message = newMessage(null, to, subject, root);
        Transport.send(message);
    }

    /**
     * Send an email
     *
     * @param from    from
     * @param to      to
     * @param subject subject
     * @param root    mail element root
     * @throws MessagingException
     */
    public void sendMessage(String from, String[] to,
                            String subject,
                            MailElement root) throws MessagingException {
        var message = newMessage(from, to, subject, root);
        Transport.send(message);
    }

    /**
     * Create an message
     *
     * @param from    from (if it is null / empty then send as default)
     * @param to      to
     * @param subject subject
     * @param root    mail element root
     * @return new message
     * @throws MessagingException
     */
    private Message newMessage(String from, String[] to, String subject, MailElement root) throws MessagingException {
        var message = new MimeMessage(smtpSession);
        if (from == null || from.isEmpty()) {
            message.setFrom();
        } else {
            message.setFrom(from);
        }
        // set to
        message.setRecipients(Message.RecipientType.TO, Arrays.stream(to).map(s -> {
            try {
                return new InternetAddress(s);
            } catch (AddressException e) {
                throw new MailException(e);
            }
        }).toArray(InternetAddress[]::new));
        // set subject
        message.setSubject(subject);
        var body = root.toPart();
        try {
            // set content
            message.setContent(body.getContent(), body.getContentType());
        } catch (IOException e) {
            throw new MailException(e);
        }
        // set as draft
        message.setFlag(Flags.Flag.DRAFT, true);
        return message;
    }
}
