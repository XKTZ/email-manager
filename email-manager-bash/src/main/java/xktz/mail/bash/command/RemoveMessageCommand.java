package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.xkamework.annotation.Component;

/**
 * Remove file command
 *
 * @author XKTZ
 * @date 2022-06-23
 */
@Component
public class RemoveMessageCommand implements EmailBashCommand {
    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        controller.folderOn().removeMessage(Integer.parseInt(args[0]));
    }

    @Override
    public String argumentsHelp() {
        return "<id>";
    }

    @Override
    public String descriptionHelp() {
        return "Remove a message";
    }
}
