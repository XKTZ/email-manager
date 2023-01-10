package xktz.mail.ui.backend.error;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xktz.mail.ui.backend.error.exception.AccountConnectedException;
import xktz.mail.ui.backend.error.exception.AccountNotConnectedException;
import xktz.mail.ui.backend.result.FailedResult;


/**
 * Exception handler for mail account
 *
 * @author XKTZ
 * @date 2022-06-27
 */
@ControllerAdvice
@Slf4j
public class MailAccountExceptionHandler {

    @ExceptionHandler(AccountNotConnectedException.class)
    @ResponseBody
    public FailedResult handleAccountNotConnected(AccountNotConnectedException e) {
        log.info("Operation failed, account not logged login");
        return new FailedResult(e.getMessage());
    }

    @ExceptionHandler(AccountConnectedException.class)
    @ResponseBody
    public FailedResult handleAccountConnected(AccountConnectedException e) {
        log.info("Operation failed, account already logged in");
        return new FailedResult(e.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseBody
    public FailedResult handleMessagingException(MessagingException e) {
        e.printStackTrace();
        return new FailedResult(e.getMessage());
    }
}
