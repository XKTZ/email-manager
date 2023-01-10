package xktz.mail.ui.backend.web.connection.service;

import org.springframework.stereotype.Service;
import xktz.mail.account.MailAccount;
import xktz.mail.ui.backend.error.exception.AccountConnectedException;
import xktz.mail.ui.backend.web.connection.aspect.LoginCheck;

/**
 * Service for an account connection
 *
 * @author XKTZ
 * @date 2022-06-27
 */
@Service
public class ConnectionService {

    /**
     * The mail account
     */
    private MailAccount mailAccount;

    /**
     * Check if the service is connected
     *
     * @return is connected or not
     */
    public synchronized boolean isConnected() {
        return mailAccount != null;
    }

    /**
     * Connect to the mail account
     *
     * @param imapAddress imap address
     * @param imapPort    imap port
     * @param smtpAddress smtp address
     * @param smtpPort    smtp port
     * @param username    username
     * @param password    password
     */
    public synchronized void connect(String imapAddress, int imapPort,
                                     String smtpAddress, int smtpPort,
                                     String username, String password) {
        if (isConnected()) {
            throw new AccountConnectedException();
        }
        this.mailAccount = new MailAccount(imapAddress, imapPort, smtpAddress, smtpPort, username, password);
    }

    public MailAccount getMailAccount() {
        return mailAccount;
    }
}
