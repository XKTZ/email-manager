package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.xkamework.annotation.Component;

/**
 * Command remove folder
 *
 * @author XKTZ
 * @date 2022-06-23
 */
@Component
public class RemoveFolderCommand implements EmailBashCommand{
    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        var name = args[0];
        controller.folderOn().removeFolder(name);
    }

    @Override
    public String argumentsHelp() {
        return "<name>";
    }

    @Override
    public String descriptionHelp() {
        return "Remove the folder";
    }
}
