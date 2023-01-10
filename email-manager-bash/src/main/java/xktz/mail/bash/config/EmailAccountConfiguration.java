package xktz.mail.bash.config;

import xktz.xkamework.annotation.*;

import java.util.Properties;

/**
 * Configuration for email account
 *
 * @author XKTZ
 * @date 2022-06-16
 */
@BeanConfiguration
public class EmailAccountConfiguration {

    /**
     * Key for imap server
     */
    private static final String KEY_IMAP_SERVER = "imapServer";

    /**
     * Key for imap port
     */
    private static final String KEY_IMAP_PORT = "imapPort";

    /**
     * Key for smtp server
     */
    private static final String KEY_SMTP_SERVER = "smtpServer";

    /**
     * Key for smtp port
     */
    private static final String KEY_SMTP_PORT = "smtpPort";

    /**
     * Key for username
     */
    private static final String KEY_USERNAME = "username";

    /**
     * Key for password
     */
    private static final String KEY_PASSWORD = "password";

    @Autowired
    @Qualifier("emailConfiguration")
    private Properties emailConfiguration;

    @Bean("imapServer")
    public String imapServer() {
        return emailConfiguration.get(KEY_IMAP_SERVER).toString();
    }

    @Bean("imapPort")
    public int imapPort() {
        return Integer.parseInt(emailConfiguration.get(KEY_IMAP_PORT).toString());
    }

    @Bean("smtpServer")
    public String smtpServer() {
        return emailConfiguration.get(KEY_SMTP_SERVER).toString();
    }

    @Bean("smtpPort")
    public int smtpPort() {
        return Integer.parseInt(emailConfiguration.get(KEY_SMTP_PORT).toString());
    }

    @Bean("username")
    public String username() {
        return emailConfiguration.get(KEY_USERNAME).toString();
    }

    @Bean("password")
    public String password() {
        return emailConfiguration.get(KEY_PASSWORD).toString();
    }
}
