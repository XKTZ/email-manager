package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;

/**
 * Handle bash command
 *
 * @author XKTZ
 * @date 2022-06-18
 */
public interface EmailBashCommand {
    /**
     * Handle a command
     *
     * @param args       arguments
     * @param controller controller
     * @param terminal   terminal
     * @throws MessagingException
     */
    void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException;

    /**
     * Help to identify arguments
     * IT IS REQUIRED TO CONTROL ARGUMENT TYPE DESCRIPTION IN 20 WORDS
     *
     * @return argument help string
     */
    default String argumentsHelp() {
        return "";
    }

    /**
     * Description of command
     * IT IS REQUIRED TO CONTROL DESCRIPTION IN 50 WORDS
     *
     * @return description
     */
    default String descriptionHelp() {
        return "";
    }
}
