package xktz.mail.account;

/**
 * Exception when cannot find a message
 *
 * @author XKTZ
 * @date 2022-06-15
 */
public class MailFileNotFoundException extends RuntimeException {
    private static final String STR_FORMAT = "Cannot find file %s";

    public MailFileNotFoundException(Object name) {
        super(STR_FORMAT.formatted(name.toString()));
    }
}
