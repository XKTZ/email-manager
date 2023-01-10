package xktz.mail.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import xktz.mail.MailException;
import xktz.mail.element.MailElement;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import static jakarta.mail.Flags.Flag.*;

/**
 * A mail message
 *
 * @author XKTZ
 * @date 2022-06-13
 */
public class MailMessage implements MailFile<Integer> {

    /**
     * The message
     */
    private Message message;

    /**
     * The id
     */
    private Integer id;

    /**
     * The display id
     */
    private String subject;

    /**
     * Email from
     */
    private InternetAddress[] from;

    /**
     * Email to
     */
    private InternetAddress[] to;

    /**
     * Sent date
     */
    private Date date;

    /**
     * Mail Element
     */
    private MailElement element;

    public MailMessage(Message message) throws MessagingException {
        this.message = message;
        this.id = message.getMessageNumber();
        this.subject = message.getSubject();
        this.from = Arrays.stream(message.getFrom()).map(InternetAddress.class::cast).toArray(InternetAddress[]::new);
        var recip = message.getRecipients(Message.RecipientType.TO);
        this.to = Arrays.stream(recip == null ? new InternetAddress[0] : recip)
                .map(InternetAddress.class::cast).toArray(InternetAddress[]::new);
        this.date = message.getSentDate();
    }

    /**
     * All from addresses
     *
     * @return from addresses
     */
    public InternetAddress[] from() {
        return from;
    }

    /**
     * All to addresses
     *
     * @return to addresses
     */
    public InternetAddress[] to() {
        return to;
    }

    /**
     * Get from names
     *
     * @return from
     */
    @JsonProperty("from")
    public String[] getFrom() {
        return Arrays.stream(from).map(InternetAddress::getAddress).toArray(String[]::new);
    }

    /**
     * Get to
     *
     * @return to
     */
    @JsonProperty("to")
    public String[] getTo() {
        return Arrays.stream(to).map(InternetAddress::getAddress).toArray(String[]::new);
    }

    /**
     * Get date
     *
     * @return date
     */
    @JsonProperty("date")
    public Date getDate() {
        return date;
    }

    /**
     * Get the mail element
     *
     * @return mail element
     */
    public MailElement element() {
        if (this.element == null) {
            this.element = MailElement.parse(this.message);
        }
        return this.element;
    }

    /**
     * Check if this mail is read or not
     *
     * @return read or not
     */
    public MailStatus status() throws MailException {
        try {
            var flags = message.getFlags();
            if (flags.contains(SEEN)) {
                return MailStatus.READ;
            } else if (flags.contains(DRAFT)) {
                return MailStatus.DRAFT;
            } else {
                return MailStatus.UNREAD;
            }
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    /**
     * Get message
     *
     * @return message
     */
    public Message message() {
        return message;
    }

    /**
     * Set a flag
     *
     * @param flag  flag
     * @param value value
     */
    public void flag(Flags.Flag flag, boolean value) throws MailException {
        try {
            message.setFlag(flag, value);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    @Override
    public Integer id() {
        return this.id;
    }

    @Override
    public String displayName() {
        return subject;
    }

    @Override
    public MailFileType getType() {
        return MailFileType.MESSAGE;
    }

    /**
     * Get mail message from message
     *
     * @param message message
     * @return mail message
     */
    public static MailMessage of(Message message) {
        try {
            return new MailMessage(message);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    /**
     * Get mail message from message
     *
     * @param message message
     * @return mail message
     */
    public static MailMessage draftOf(Message message, Runnable sender) {
        try {
            return new DraftMailMessage(message, sender);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public enum MailStatus {
        DRAFT,
        UNREAD,
        READ
    }
}
