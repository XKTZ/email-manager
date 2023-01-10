package xktz.mail.ui.backend.web.file.controller;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xktz.mail.ui.backend.result.Result;
import xktz.mail.ui.backend.web.account.aspect.AccountReading;
import xktz.mail.ui.backend.web.account.aspect.AccountWriting;
import xktz.mail.ui.backend.web.account.domain.*;
import xktz.mail.ui.backend.web.account.service.AccountService;
import xktz.mail.ui.backend.web.file.MessageInformation;
import xktz.mail.ui.backend.web.file.service.FileService;

import java.util.List;

/**
 * File controller
 *
 * @author XKTZ
 * @date 2022-07-03
 */
@RestController
public class FileController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/mail/{id}", method = RequestMethod.GET)
    @ResponseBody
    @AccountReading
    public Result<MessageInformation> readMessage(@PathVariable("id") Integer id) throws MessagingException {
        return Result.successResultOf(new MessageInformation(fileService.getMessage(id)));
    }

    @RequestMapping(value = "/mail/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result<?> removeMessage(@PathVariable("id") Integer id) throws MessagingException {
        fileService.removeMessage(id);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/mail/move/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Result<?> moveMessage(@PathVariable("id") Integer id, @RequestBody MailAccountPath path) throws MessagingException {
        fileService.moveMessage(id, path.getPaths().toArray(String[]::new));
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/directory/{name}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> makeDirectory(@PathVariable("name") String name) throws MessagingException {
        fileService.makeDirectory(name);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/directory/{name}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result<?> removeDirectory(@PathVariable("name") String name) throws MessagingException {
        fileService.removeDirectory(name);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping("/list")
    @ResponseBody
    @AccountReading
    public Result<FolderInformation> listFiles() throws MessagingException {
        return Result.successResultOf(new FolderInformation(accountService.folderOn(), accountService.list()));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.PUT)
    @ResponseBody
    @AccountWriting
    public Result<?> refresh() throws MessagingException {
        fileService.refresh();
        return Result.EMPTY_SUCCESS_RESULT;
    }
}
