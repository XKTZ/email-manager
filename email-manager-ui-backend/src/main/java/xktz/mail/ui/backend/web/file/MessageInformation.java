package xktz.mail.ui.backend.web.file;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import xktz.mail.account.MailMessage;
import xktz.mail.element.MailElement;

/**
 * Information of message
 *
 * @author XKTZ
 * @date 2022-07-05
 */
public class MessageInformation {

    @JsonProperty("message")
    private MailMessage message;

    @JsonProperty("content")
    private MailElement content;

    public MessageInformation(MailMessage message) {
        this.message = message;
        this.content = message.element();
    }

    @JsonGetter("message")
    public MailMessage getMessage() {
        return message;
    }

    @JsonGetter("content")
    public MailElement getContent() {
        return content;
    }
}
