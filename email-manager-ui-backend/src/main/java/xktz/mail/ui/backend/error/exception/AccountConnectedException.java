package xktz.mail.ui.backend.error.exception;

/**
 * Exception that the account is already connected
 *
 * @author XKTZ
 * @date 2022-06-28
 */
public class AccountConnectedException extends RuntimeException {

    public AccountConnectedException() {
        super("Account already connected");
    }
}
