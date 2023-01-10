package xktz.mail.ui.backend.web.draft.controller;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xktz.mail.ui.backend.result.Result;
import xktz.mail.ui.backend.web.account.service.AccountService;
import xktz.mail.ui.backend.web.draft.domain.DraftMessage;
import xktz.mail.ui.backend.web.draft.domain.DraftMessageInfo;
import xktz.mail.ui.backend.web.draft.service.DraftManagementService;

import java.io.IOException;

/**
 * @author XKTZ
 * @date 2022-07-09
 */
@RestController
@RequestMapping("/draft")
public class DraftController {

    @Autowired
    private DraftManagementService draftManagementService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/list/header", method = RequestMethod.GET)
    public synchronized Result<?> listDraftHeader() throws IOException {
        return Result.successResultOf(draftManagementService.listHeader());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public synchronized Result<?> listDraftMessage() throws IOException {
        return Result.successResultOf(draftManagementService.list());
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public synchronized Result<String> createDraft(@RequestBody DraftMessageInfo info)
            throws IOException {
        String id = draftManagementService.acquireKey();
        info.getHeader().setId(id);
        draftManagementService.write(id,
                new DraftMessage(id, info.getHeader().getFrom(), info.getHeader().getTo(), info.getHeader().getSubject(),
                        info.getContent().getRoot()));
        return Result.successResultOf(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public synchronized Result<?> updateDraft(@PathVariable("id") String id, @RequestBody DraftMessageInfo info)
            throws IOException {
        info.getHeader().setId(id);
        draftManagementService.write(id, info);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public synchronized Result<DraftMessage> readDraft(@PathVariable("id") String id) throws IOException {
        return Result.successResultOf(draftManagementService.read(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public synchronized Result<?> removeDraft(@PathVariable("id") String id) throws IOException {
        draftManagementService.removeById(id);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/send/{id}", method = RequestMethod.PUT)
    public synchronized Result<?> sendDraft(@PathVariable("id") String id)throws IOException, MessagingException {
        var draft = draftManagementService.read(id);
        accountService.sendMessage(draft.getFrom(), draft.getTo(), draft.getSubject(), draft.getRoot());
        return Result.EMPTY_SUCCESS_RESULT;
    }
}
