package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.mail.account.MailFileNotFoundException;
import xktz.mail.account.MailFolder;
import xktz.xkamework.annotation.Component;

import java.util.Arrays;

/**
 * Command move a message into
 *
 * @author XKTZ
 * @date 2022-06-20
 */
@Component
public class MoveCommand implements EmailBashCommand {

    /**
     * Folder type
     */
    private static final String TYPE_FOLDER = "folder";

    /**
     * Message type
     */
    private static final String TYPE_MESSAGE = "msg";

    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        int id = Integer.parseInt(args[0]);
        String[] path = Arrays.copyOfRange(args, 1, args.length);
        MailFolder folderTo = null;
        try {
            folderTo = controller.folderOn().direct(path);
        } catch (MailFileNotFoundException e) {
            terminal.println(e.getMessage());
        }
        controller.folderOn().moveMessageTo(folderTo, controller.getMessage(id));
    }

    @Override
    public String argumentsHelp() {
        return "<id> <path>...";
    }

    @Override
    public String descriptionHelp() {
        return "Move a message to a specific path";
    }
}
