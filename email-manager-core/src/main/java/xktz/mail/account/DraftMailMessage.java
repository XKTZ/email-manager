package xktz.mail.account;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.util.function.Consumer;

/**
 * A draft message. It has a method "send", when run the "send" method,
 * the "sender" runnable gain from constructor is run
 * This class is deprecated because it only works when the object is in runtime
 * In other word, if people close program and open gain, the "runnable" no longer works
 *
 * @author XKTZ
 * @date 2022-06-22
 */
@Deprecated
public class DraftMailMessage extends MailMessage {
    private Runnable sender;

    public DraftMailMessage(Message message, Runnable sender) throws MessagingException {
        super(message);
        this.sender = sender;
    }

    public void send() {
        this.sender.run();
    }
}
