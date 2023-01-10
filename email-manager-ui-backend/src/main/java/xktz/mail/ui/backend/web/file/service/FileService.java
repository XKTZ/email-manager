package xktz.mail.ui.backend.web.file.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xktz.mail.account.MailMessage;
import xktz.mail.ui.backend.web.account.aspect.AccountReading;
import xktz.mail.ui.backend.web.account.aspect.AccountWriting;
import xktz.mail.ui.backend.web.account.domain.FolderInformation;
import xktz.mail.ui.backend.web.account.service.AccountService;
import xktz.mail.ui.backend.web.connection.aspect.LoginCheck;

/**
 * Service managing the file in gmail
 *
 * @author XKTZ
 * @date 2022-07-03
 */
@Service
public class FileService {

    @Autowired
    private AccountService accountService;

    @LoginCheck
    @AccountReading
    public MailMessage getMessage(Integer integer) throws MessagingException {
        return accountService.folderOn().messageOf(integer);
    }

    @LoginCheck
    @AccountWriting
    public void removeMessage(Integer id) throws MessagingException {
        accountService.folderOn().removeMessage(id);
    }

    @LoginCheck
    @AccountWriting
    public void makeDirectory(String dirName) throws MessagingException {
        accountService.folderOn().makeFolder(dirName);
    }

    @LoginCheck
    @AccountWriting
    public void removeDirectory(String dirName) throws MessagingException {
        accountService.folderOn().removeFolder(dirName);
    }

    @LoginCheck
    @AccountWriting
    public void moveMessage(Integer id, String[] dirPath) throws MessagingException {
        var folder = accountService.folderOn();
        folder.moveMessageTo(folder.direct(dirPath), folder.messageOf(id));
    }

    @LoginCheck
    @AccountWriting
    public void refresh() throws MessagingException {
        accountService.folderOn().refreshFolder();
    }
}
