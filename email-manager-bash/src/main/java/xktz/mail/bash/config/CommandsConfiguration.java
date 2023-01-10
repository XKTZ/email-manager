package xktz.mail.bash.config;

import xktz.mail.bash.command.*;
import xktz.mail.bash.command.*;
import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Bean;
import xktz.xkamework.annotation.BeanConfiguration;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration for commands
 *
 * @author XKTZ
 * @date 2022-06-20
 */
@BeanConfiguration
public class CommandsConfiguration {

    @Autowired
    private ShowDirectoryCommand showDirectoryCommand;

    @Autowired
    private ChangeDirectoryCommand changeDirectoryCommand;

    @Autowired
    private ReadMessageCommand readMessageCommand;

    @Autowired
    private MoveCommand moveCommand;

    @Autowired
    private MakeFolderCommand makeFolderCommand;

    @Autowired
    private SendMessageCommand sendMessageCommand;

    @Autowired
    private RemoveFolderCommand removeFolderCommand;

    @Autowired
    private RemoveMessageCommand removeMessageCommand;

    /**
     * Basic command list
     *
     * @return command list
     */
    private List<AbstractMap.SimpleEntry<String, EmailBashCommand>> basicCommandList() {
        return new ArrayList<>(List.of(
                new AbstractMap.SimpleEntry<>("ls", showDirectoryCommand),
                new AbstractMap.SimpleEntry<>("cd", changeDirectoryCommand),
                new AbstractMap.SimpleEntry<>("read", readMessageCommand),
                new AbstractMap.SimpleEntry<>("mv", moveCommand),
                new AbstractMap.SimpleEntry<>("mkdir", makeFolderCommand),
                new AbstractMap.SimpleEntry<>("rmdir", removeFolderCommand),
                new AbstractMap.SimpleEntry<>("send", sendMessageCommand),
                new AbstractMap.SimpleEntry<>("rm", removeMessageCommand)
        ));
    }

    /**
     * Command map
     * @return command map
     */
    @Bean("commandMap")
    public Map<String, EmailBashCommand> commandMap() {
        var commandList = basicCommandList();

        return commandList.stream().collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
