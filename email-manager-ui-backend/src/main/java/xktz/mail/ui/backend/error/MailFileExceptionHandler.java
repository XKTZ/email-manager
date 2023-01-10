package xktz.mail.ui.backend.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xktz.mail.account.MailFileNotFoundException;
import xktz.mail.ui.backend.result.FailedResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Exception handler for accounts
 *
 * @author XKTZ
 * @date 2022-07-03
 */
@ControllerAdvice
@Slf4j
public class MailFileExceptionHandler {
    @ExceptionHandler(MailFileNotFoundException.class)
    @ResponseBody
    public FailedResult handleMailFileNotFoundException(MailFileNotFoundException e) {
        return new FailedResult(e.getMessage());
    }
}
