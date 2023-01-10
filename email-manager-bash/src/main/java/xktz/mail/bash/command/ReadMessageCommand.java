package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.mail.element.*;
import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Component;
import xktz.xkamework.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * Command output message
 *
 * @author XKTZ
 * @date 2022-06-19
 */
@Component
public class ReadMessageCommand implements EmailBashCommand {

    @Autowired
    @Qualifier("readMessageAttachmentIdOn")
    private AtomicInteger attachmentIdOn;

    @Autowired
    @Qualifier("readMessageAttachments")
    private List<AttachmentMailElement> attachments;

    /**
     * Mail element string parser
     */
    @Autowired
    @Qualifier("mailElementParserMap")
    private Map<MailElementType, MailElementRecursiveParser> mailElementParserMap;

    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        var id = Integer.parseInt(args[0]);
        var msg = controller.getMessage(id);
        attachments.clear();
        attachmentIdOn.set(0);
        // output basic information of message
        terminal.println(msg.displayName());
        terminal.println("From: " + String.join(", ", Arrays.stream(msg.getFrom()).map(s -> '<' + s + '>').toList()));
        terminal.println("To: " + String.join(" ", Arrays.stream(msg.getTo()).map(s -> '<' + s + '>').toList()));
        terminal.println("Date: " + msg.getDate());
        terminal.printDivider();
        parse(msg.element(), terminal);
    }

    /**
     * parse element
     *
     * @param element  element
     * @param terminal terminal
     */
    private void parse(MailElement element, Terminal terminal) {
        mailElementParserMap.get(element.getType()).parse(element, terminal, this::parse);
    }

    @Override
    public String argumentsHelp() {
        return "<ID>";
    }

    @Override
    public String descriptionHelp() {
        return "Read a message";
    }

    /**
     * Parser for mail element
     *
     * @author XKTZ
     * @date 2022-06-20
     */
    public interface MailElementRecursiveParser {

        /**
         * Parse an element
         *
         * @param element  element
         * @param terminal terminal
         * @param parser   parser
         */
        void parse(MailElement element, Terminal terminal, BiConsumer<MailElement, Terminal> parser);
    }
}
