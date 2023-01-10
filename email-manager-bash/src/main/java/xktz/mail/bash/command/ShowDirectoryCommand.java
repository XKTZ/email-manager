package xktz.mail.bash.command;

import jakarta.mail.MessagingException;
import xktz.mail.bash.EmailManagerController;
import xktz.mail.bash.Terminal;
import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Component;
import xktz.xkamework.annotation.Qualifier;

import java.util.Arrays;

/**
 * Command show directory
 *
 * @author XKTZ
 * @date 2022-06-19
 */
@Component
public class ShowDirectoryCommand implements EmailBashCommand {

    /**
     * No subject
     */
    private static String NO_SUBJECT = "<No Subject>";

    /**
     * Format for messages
     */
    private String messageFormat;

    /**
     * message title
     */
    private String messageTitle;

    /**
     * id length
     */
    private int idLength;

    /**
     * subject length
     */
    private int subjectLength;

    /**
     * from length
     */
    private int fromLength;

    /**
     * state length
     */
    private int stateLength;

    /**
     * Create command by providing length of each column (the column length of subject & from are divided by 2 for SPACES)
     *
     * @param idLength      length of id column
     * @param subjectLength length of subject column
     * @param fromLength    length of from column
     */
    @Autowired
    public ShowDirectoryCommand(@Qualifier("showDirectoryIdLength") int idLength,
                                @Qualifier("showDirectorySubjectLength") int subjectLength,
                                @Qualifier("showDirectoryFromLength") int fromLength,
                                @Qualifier("showDirectoryStateLength") int stateLength) {
        this.idLength = idLength;
        this.subjectLength = subjectLength;
        this.fromLength = fromLength;
        this.stateLength = stateLength;
        messageFormat = "%-" + idLength + "d%-" + subjectLength + "s%-" + fromLength + "s%-" + stateLength + "s";
        messageTitle = ("%-" + idLength + "s%-" + subjectLength + "s%-" + fromLength + "s%-" + stateLength + "s")
                .formatted("ID", "Subject", "From", "State");
    }

    @Override
    public void handle(String[] args, EmailManagerController controller, Terminal terminal) throws MessagingException {
        var folders = controller.listFolders();
        var msgs = controller.listMessages();

        terminal.println("Folders: ");
        if (folders.length > 0) {
            Arrays.stream(controller.listFolders())
                    .forEach(folder -> terminal.println('-', folder.displayName()));
        } else {
            terminal.println("None.");
        }

        terminal.printDivider();

        terminal.println("Messages: ");
        terminal.println(messageTitle);
        if (msgs.length > 0) {
            Arrays.stream(msgs).forEach(mailMessage -> {
                var id = mailMessage.id();
                String displayName = mailMessage.displayName(),
                        from = String.join(",", mailMessage.getFrom()),
                        state = mailMessage.status().toString();
                displayName = Terminal.ellipsis(displayName == null ? NO_SUBJECT : displayName, subjectLength - 2);
                from = Terminal.ellipsis(from, fromLength - 2);
                state = Terminal.ellipsis(state, stateLength);
                terminal.println(messageFormat.formatted(id, displayName, from, state));
            });
        } else {
            terminal.println("None.");
        }
    }

    @Override
    public String descriptionHelp() {
        return "Show data in directory";
    }
}
