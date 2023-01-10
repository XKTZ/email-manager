package xktz.mail.ui.backend.web.account.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xktz.mail.account.MailFile;
import xktz.mail.account.MailFolder;
import xktz.mail.element.MailElement;
import xktz.mail.ui.backend.web.account.aspect.AccountReading;
import xktz.mail.ui.backend.web.account.aspect.AccountWriting;
import xktz.mail.ui.backend.web.connection.aspect.LoginCheck;
import xktz.mail.ui.backend.web.connection.service.ConnectionService;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Account service
 *
 * @author XKTZ
 * @date 2022-07-01
 */
@Service
public class AccountService {

    @Autowired
    private ConnectionService connectionService;

    private MailFolder mailFolderOn;

    /**
     * Make sure mail folder on is initialized
     */
    private synchronized void assertInitialized() throws MessagingException {
        if (mailFolderOn == null) {
            mailFolderOn = connectionService.getMailAccount().root();
        }
    }

    /**
     * Direct from root by paths
     *
     * @param paths paths
     * @return folder directing
     */
    @LoginCheck
    @AccountReading
    public MailFolder directFromRoot(String... paths) throws MessagingException {
        assertInitialized();
        return connectionService.getMailAccount().direct(paths);
    }

    /**
     * Change to another folder
     *
     * @param paths path
     * @return another folder
     * @throws MessagingException
     */
    @LoginCheck
    @AccountWriting
    public MailFolder cd(String... paths) throws MessagingException {
        assertInitialized();
        mailFolderOn = mailFolderOn.direct(paths);
        return mailFolderOn;
    }

    /**
     * Get the mail folder on
     *
     * @return mail folder on
     * @throws MessagingException
     */
    @LoginCheck
    @AccountReading
    public MailFolder folderOn() throws MessagingException {
        assertInitialized();
        return mailFolderOn;
    }

    /**
     * List all stuff under mail folder
     *
     * @return all stuff
     * @throws MessagingException
     */
    @LoginCheck
    @AccountReading
    public MailFile<?>[] list() throws MessagingException {
        assertInitialized();
        return mailFolderOn.list();
    }


    /**
     * Send a message with account
     *
     * @param from    from
     * @param to      to
     * @param subject subject
     * @param content content
     * @throws MessagingException
     */
    @LoginCheck
    public void sendMessage(String from, String[] to, String subject, MailElement content) throws MessagingException {
        connectionService.getMailAccount().sendMessage(from, to, subject, content);
    }
}
