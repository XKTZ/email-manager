package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.mail.account.MailFileNotFoundException;
import xktz.xkamework.annotation.Component;

/**
 * Command changing directory
 *
 * @author XKTZ
 * @date 2022-06-19
 */
@Component
public class ChangeDirectoryCommand implements EmailBashCommand {
    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        try {
            controller.cd(args);
        } catch (MailFileNotFoundException e) {
            terminal.println(e.getMessage());
        }
    }

    @Override
    public String argumentsHelp() {
        return "<path>...";
    }

    @Override
    public String descriptionHelp() {
        return "Go to a specific path (cannot go to parent)";
    }
}
