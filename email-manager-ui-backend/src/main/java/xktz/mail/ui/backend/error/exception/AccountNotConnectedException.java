package xktz.mail.ui.backend.error.exception;

/**
 * Exception that the account is not connected
 *
 * @author XKTZ
 * @date 2022-06-27
 */
public class AccountNotConnectedException extends RuntimeException {

    public AccountNotConnectedException() {
        super("Account is not connected");
    }
}
