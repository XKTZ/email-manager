package xktz.mail.ui.backend.web.account.controller;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xktz.mail.account.MailFolder;
import xktz.mail.ui.backend.result.Result;
import xktz.mail.ui.backend.web.account.aspect.AccountReading;
import xktz.mail.ui.backend.web.account.aspect.AccountWriting;
import xktz.mail.ui.backend.web.account.domain.FolderInformation;
import xktz.mail.ui.backend.web.account.domain.MailAccountPath;
import xktz.mail.ui.backend.web.account.service.AccountService;

/**
 * Account controller
 *
 * @author XKTZ
 * @date 2022-07-01
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Get the folder on
     *
     * @return folder on
     * @throws MessagingException
     */
    @RequestMapping("/on")
    @ResponseBody
    @AccountReading
    public Result<MailFolder> getFolderOn() throws MessagingException {
        return Result.successResultOf(accountService.folderOn());
    }

    /**
     * Get information of the folder on (name and children)
     *
     * @return files
     */
    @RequestMapping("/info")
    @ResponseBody
    @AccountReading
    public Result<FolderInformation> info() throws MessagingException {
        return Result.successResultOf(new FolderInformation(accountService.folderOn(), accountService.list()));
    }

    @RequestMapping(value = "/cd", method = RequestMethod.PUT)
    @ResponseBody
    @AccountWriting
    public Result<?> cd(@RequestBody MailAccountPath paths) throws MessagingException {
        accountService.cd(paths.toArray());
        return Result.EMPTY_SUCCESS_RESULT;
    }
}
