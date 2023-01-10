package xktz.mail.bash.error;

/**
 * Exception happens in mail bash
 *
 * @author XKTZ
 * @date 2022-06-16
 */
public class MailBashException extends RuntimeException {

    public MailBashException(Exception e) {
        super(e);
    }
}
