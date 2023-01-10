package xktz.mail.ui.backend.web.draft.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import xktz.mail.element.HTMLMailElement;
import xktz.mail.element.MailElement;

import java.io.IOException;

/**
 * Content of draft message, for now it only supports HTML and/or text
 *
 * @author XKTZ
 * @date 2022-07-08
 */
@JsonDeserialize(using = DraftMessageContent.DraftMessageContentDeserializer.class)
public class DraftMessageContent {

    /**
     * HTML
     */
    @JsonProperty("root")
    private MailElement root;

    public DraftMessageContent() {
    }

    public static DraftMessageContent of(MailElement root) {
        var content = new DraftMessageContent();
        content.root = root;
        return content;
    }

    public void setRoot(MailElement root) {
        this.root = root;
    }

    @JsonGetter("root")
    public MailElement getRoot() {
        return root;
    }

    public static DraftMessageContent of(DraftMessage msg) {
        var msgContent = new DraftMessageContent();
        msgContent.setRoot(msg.getRoot());
        return msgContent;
    }

    static class DraftMessageContentDeserializer extends StdDeserializer<DraftMessageContent> {

        public DraftMessageContentDeserializer() {
            this(null);
        }

        public DraftMessageContentDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public DraftMessageContent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            JsonNode contentNode = jsonParser.getCodec().readTree(jsonParser);
            if (!contentNode.has("root")) {
                return DraftMessageContent.of(HTMLMailElement.of("", ""));
            }
            JsonNode node = contentNode.get("root");
            String html = node.has("html") ? node.get("html").asText(): null;
            String text = node.has("text") ?  node.get("text").asText() : null;
            if (html == null && text == null) {
                return DraftMessageContent.of(HTMLMailElement.of("", ""));
            } else if (html == null) {
                return DraftMessageContent.of(HTMLMailElement.textOf(text));
            } else if (text == null) {
                return DraftMessageContent.of(HTMLMailElement.htmlOf(html));
            } else {
                return DraftMessageContent.of(HTMLMailElement.of(html, text));
            }
        }
    }
}
