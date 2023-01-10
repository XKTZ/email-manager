package xktz.mail.bash;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import xktz.mail.account.MailAccount;
import xktz.mail.account.MailFile;
import xktz.mail.account.MailFolder;
import xktz.mail.account.MailMessage;
import xktz.mail.element.MailElement;
import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Component;
import xktz.xkamework.annotation.Qualifier;
import xktz.xkamework.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller of email manager
 *
 * @author XKTZ
 * @date 2022-06-16
 */
@Component
public class EmailManagerController {

    /**
     * Account
     */
    private MailAccount mailAccount;

    /**
     * Folder on
     */
    private MailFolder folderOn;

    @Autowired
    public EmailManagerController(@Qualifier("imapServer") String imapServer,
                                  @Qualifier("imapPort") int imapPort,
                                  @Qualifier("smtpServer") String smtpServer,
                                  @Qualifier("smtpPort") int smtpPort,
                                  @Qualifier("username") String username,
                                  @Qualifier("password") String password) throws MessagingException {
        mailAccount = new MailAccount(imapServer, imapPort, smtpServer, smtpPort, username, password);
        folderOn = mailAccount.root();
    }

    /**
     * List all files
     *
     * @return files
     * @throws MessagingException
     */
    public MailFile<?>[] list() throws MessagingException {
        return folderOn.list();
    }

    /**
     * List all folders
     *
     * @return folders
     * @throws MessagingException
     */
    public MailFolder[] listFolders() throws MessagingException {
        return folderOn.listFolders();
    }

    /**
     * List all messages
     *
     * @return messages
     * @throws MessagingException
     */
    public MailMessage[] listMessages() throws MessagingException {
        try {
            return folderOn.listMessages();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get message with specific id
     *
     * @param id id
     * @return message
     * @throws MessagingException
     */
    public MailMessage getMessage(int id) throws MessagingException {
        return folderOn.messageOf(id);
    }

    /**
     * Move to folder
     *
     * @param paths path
     * @throws MessagingException
     */
    public void cd(String... paths) throws MessagingException {
        if (paths.length == 0) {
            return;
        }
        if (paths[0].isEmpty()) {
            paths = Arrays.copyOfRange(paths, 1, paths.length);
            folderOn = mailAccount.direct(paths);
        } else {
            folderOn = folderOn.direct(paths);
        }
    }

    /**
     * Go to parent
     *
     * @throws MessagingException
     */
    public void parent() throws MessagingException {
        if (folderOn.parent() != null) {
            folderOn = folderOn.parent();
        }
    }

    /**
     * send a new email
     *
     * @param to      sending to
     * @param subject subject
     * @param root    mail element root
     * @throws MessagingException
     */
    public void sendMessage(String[] to, String subject, MailElement root) throws MessagingException {
        mailAccount.sendMessage(to, subject, root);
    }

    /**
     * send a new email
     *
     * @param from    from
     * @param to      sending to
     * @param subject subject
     * @param root    mail element root
     * @throws MessagingException
     */
    public void sendMessage(String from, String[] to, String subject, MailElement root) throws MessagingException {
        mailAccount.sendMessage(from, to, subject, root);
    }

    /**
     * Get the folder on
     *
     * @return the folder on
     */
    public MailFolder folderOn() {
        return folderOn;
    }
}
