package xktz.mail.bash.command.config;

import xktz.mail.bash.command.ReadMessageCommand;
import xktz.mail.element.*;
import xktz.xkamework.annotation.Bean;
import xktz.xkamework.annotation.BeanConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XKTZ
 * @date 2022-06-20
 */
@BeanConfiguration
public class ReadMessageConfiguration {

    private AtomicInteger idOn = new AtomicInteger();

    private List<AttachmentMailElement> attachments = new ArrayList<>();

    @Bean("readMessageAttachmentIdOn")
    public AtomicInteger attachmentIdOn() {
        return idOn;
    }

    @Bean("readMessageAttachments")
    public List<AttachmentMailElement> attachmentList() {
        return attachments;
    }

    @Bean("mailElementParserMap")
    public Map<MailElementType, ReadMessageCommand.MailElementRecursiveParser> mailElementParser() {
        return new HashMap<>() {{
            put(MailElementType.HTML, (element, terminal, parser) -> terminal.println(((HTMLMailElement) element).getText()));
            put(MailElementType.COLLECTION, (element, terminal, parser) -> {
                for (var mailElement : ((MultipartMailElement) (element)).getElements()) {
                    parser.accept(mailElement, terminal);
                }
            });
            put(MailElementType.COMBINATION, (element, terminal, parser) -> {
                for (var mailElement : ((RelatedMailElement) element).getElements()) {
                    parser.accept(mailElement, terminal);
                }
                for (var mailElement : ((RelatedMailElement) element).images()) {
                    parser.accept(mailElement, terminal);
                }
            });
            put(MailElementType.IMAGE, (element, terminal, parser) -> {
                // nothing to do with images
            });
            put(MailElementType.ATTACHMENT, (element, terminal, parser) -> {
                var file = (AttachmentMailElement) element;
                terminal.println("[Attachment %s. Id: %d]".formatted(file.getName(), idOn.getAndIncrement()));
                attachments.add(file);
            });
        }};
    }
}
