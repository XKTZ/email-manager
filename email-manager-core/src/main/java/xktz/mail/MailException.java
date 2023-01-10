package xktz.mail;

/**
 * Exception happen in mail
 *
 * @author XKTZ
 * @date 2022-06-14
 */
public class MailException extends RuntimeException {


    public MailException(Exception cause) {
        super(cause);
    }

    public MailException(String msg) {
        super(msg);
    }
}
