package xktz.mail.ui.backend.error.exception;

/**
 * Exception when some required file is not found
 *
 * @author XKTZ
 * @date 2022-07-08
 */
public class RequiredFileNotFoundException extends RuntimeException {

    public RequiredFileNotFoundException(String file) {
        super("Required file %s is not found".formatted(file));
    }
}
