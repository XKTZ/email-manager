package xktz.mail.bash;

import jakarta.mail.*;
import xktz.mail.bash.command.*;
import xktz.mail.MailException;
import xktz.mail.bash.command.EmailBashCommand;
import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Component;
import xktz.xkamework.annotation.Qualifier;
import xktz.xkamework.json.JsonApplicationContext;

import java.util.*;

/**
 * Bash (main class)
 *
 * @author XKTZ
 * @date 2022-06-16
 */
@Component
public class Bash {

    @Autowired
    @Qualifier("commandMap")
    private Map<String, EmailBashCommand> commandMap;

    @Autowired
    private Terminal terminal;

    @Autowired
    private EmailManagerController controller;

    /**
     * Run the program
     */
    public void run() {
        String[] commands;
        while ((commands = readCommand()).length == 0 || !commands[0].equals("exit")) {
            if (commands.length == 0 || "".equals(commands[0])) {
                continue;
            }
            if ("help".equals(commands[0])) {
                terminal.println("%-10s%-20s%-50s".formatted("Command", "Arguments", "Description"));
                for (var entry: commandMap.entrySet()) {
                    terminal.println("%-10s%-20s%-50s".formatted(entry.getKey(), entry.getValue().argumentsHelp(), entry.getValue().descriptionHelp()));
                }
            } else {
                var args = Arrays.copyOfRange(commands, 1, commands.length);
                try {
                    commandMap.get(commands[0]).handle(args, controller, terminal);
                } catch (MailException | MessagingException e) {
                    terminal.println("Error: ", e.getMessage());
                }
            }
        }
    }

    /**
     * Read command
     *
     * @return command
     */
    private String[] readCommand() {
        return terminal.nextLine(controller.folderOn().fullName() + "> ").split(" ");
    }

    public static void main(String[] args) throws Exception {
        var applicationContext = new JsonApplicationContext("/config.json");
        applicationContext.getBean(Bash.class).run();
    }
}
