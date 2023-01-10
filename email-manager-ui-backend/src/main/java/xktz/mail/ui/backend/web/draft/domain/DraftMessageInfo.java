package xktz.mail.ui.backend.web.draft.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author XKTZ
 * @date 2022-07-08
 */
public class DraftMessageInfo {

    @JsonProperty("header")
    private DraftMessageHeader header;

    @JsonProperty("content")
    private DraftMessageContent content;

    public DraftMessageInfo() {
    }

    @JsonGetter("header")
    public DraftMessageHeader getHeader() {
        return header;
    }

    @JsonSetter("header")
    public void setHeader(DraftMessageHeader header) {
        this.header = header;
    }

    @JsonGetter("content")
    public DraftMessageContent getContent() {
        return content;
    }

    @JsonSetter("content")
    public void setContent(DraftMessageContent content) {
        this.content = content;
    }

}
