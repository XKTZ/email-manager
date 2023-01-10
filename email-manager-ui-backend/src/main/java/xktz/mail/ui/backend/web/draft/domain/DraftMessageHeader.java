package xktz.mail.ui.backend.web.draft.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Basic header of draft message
 *
 * @author XKTZ
 * @date 2022-07-08
 */
public class DraftMessageHeader {

    @JsonProperty("id")
    private String id;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String[] to;

    @JsonProperty("subject")
    private String subject;

    public DraftMessageHeader() {
    }

    public String getId() {
        return id;
    }

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

    @JsonGetter("to")
    public String[] getTo() {
        return to;
    }

    @JsonSetter("to")
    public void setTo(String[] to) {
        this.to = to;
    }

    @JsonGetter("subject")
    public String getSubject() {
        return subject;
    }

    @JsonSetter("subject")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static DraftMessageHeader of(DraftMessage msg) {
        var msgHeader = new DraftMessageHeader();
        msgHeader.id = msg.getId();
        msgHeader.from = msg.getFrom();
        msgHeader.to = msg.getTo();
        msgHeader.subject = msg.getSubject();
        return msgHeader;
    }
}
