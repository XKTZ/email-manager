package xktz.mail.ui.backend.web.draft.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import xktz.mail.element.MailElement;

/**
 * A draft message not sent yet
 *
 * @author XKTZ
 * @date 2022-07-06
 */
public class DraftMessage {

    @JsonProperty("id")
    private String id;

    /**
     * From
     */
    @JsonProperty("from")
    private String from;

    /**
     * To
     */
    @JsonProperty("to")
    private String[] to;

    /**
     * Subject
     */
    @JsonProperty("subject")
    private String subject;

    /**
     * Root
     */
    @JsonProperty("root")
    private MailElement root;

    public DraftMessage(String key, String from, String[] to, String subject, MailElement root) {
        this.id = key;
        this.from = from;
        this.subject = subject;
        this.to = to;
        this.root = root;
    }

    @JsonSetter("id")
    public String getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonGetter("from")
    public String getFrom() {
        return from;
    }

    @JsonSetter("from")
    public void setFrom(String from) {
        this.from = from;
    }

    @JsonGetter("subject")
    public String getSubject() {
        return subject;
    }

    @JsonSetter("subject")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonGetter("to")
    public String[] getTo() {
        return to;
    }

    @JsonSetter("to")
    public void setTo(String[] to) {
        this.to = to;
    }

    @JsonGetter("root")
    public MailElement getRoot() {
        return root;
    }

    @JsonSetter("root")
    public void setRoot(MailElement root) {
        this.root = root;
    }
}
