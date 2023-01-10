package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.mail.MailException;
import xktz.mail.element.HTMLMailElement;
import xktz.mail.element.MailElement;
import xktz.xkamework.annotation.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Command sent a message
 *
 * @author XKTZ
 * @date 2022-06-22
 */
@Component
public class SendMessageCommand implements EmailBashCommand {
    /**
     * Message send types
     */
    private static final Map<String, Function<String, MailElement>> MAP_PATH_TO_CONTENT = new ConcurrentHashMap<>() {{
        put("html", (path) -> {
            try {
                String html = Files.readString(Path.of(path), StandardCharsets.UTF_8);
                return HTMLMailElement.htmlOf(html);
            } catch (IOException e) {
                throw new MailException(e);
            }
        });
        put("text", (path) -> {
            try {
                String text = Files.readString(Path.of(path), StandardCharsets.UTF_8);
                return HTMLMailElement.textOf(text);
            } catch (IOException e) {
                throw new MailException(e);
            }
        });
    }};

    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        var type = args[0];
        var filePath = args[1];
        var from = terminal.nextLine("From: ");
        var to = terminal.nextLine("To (Seperated with space): ").split(" ");
        var subject = terminal.nextLine("Subject: ");
        try {
            var content = MAP_PATH_TO_CONTENT.get(type).apply(filePath);
            controller.sendMessage(from, to, subject, content);
        } catch (MailException e) {
            terminal.println("Error: ", e.getMessage());
        }
    }

    @Override
    public String argumentsHelp() {
        return "<tp> <fpath>";
    }

    @Override
    public String descriptionHelp() {
        return "Send <fpath> (NO space) type <tp> html/plain";
    }
}
