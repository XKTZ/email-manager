package xktz.mail.ui.backend.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xktz.mail.ui.backend.result.FailedResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XKTZ
 * @date 2022-07-03
 */
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public FailedResult handleUndefinedMethods(Throwable e) {
        log.info(e.getMessage());
        List<String> errorList = new ArrayList<>();
        do {
            errorList.add(e.getClass() + ": " + e.getMessage());
        } while ((e = e.getCause()) != null);
        return new FailedResult(String.join("\n", errorList));
    }
}
