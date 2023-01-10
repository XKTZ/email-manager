package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.xkamework.annotation.Component;

/**
 * Command make a folder
 *
 * @author XKTZ
 * @date 2022-06-21
 */
@Component
public class MakeFolderCommand implements EmailBashCommand {


    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        controller.folderOn().makeFolder(args[0]);
    }

    @Override
    public String argumentsHelp() {
        return "<name>";
    }

    @Override
    public String descriptionHelp() {
        return "Make a folder called <name>";
    }
}
