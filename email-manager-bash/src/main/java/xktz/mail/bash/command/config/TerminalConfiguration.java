package xktz.mail.bash.command.config;

import xktz.xkamework.annotation.Bean;
import xktz.xkamework.annotation.BeanConfiguration;

/**
 * @author XKTZ
 * @date 2022-06-20
 */
@BeanConfiguration
public class TerminalConfiguration {
    @Bean("terminalWidth")
    public int terminalWidth() {
        return 90;
    }
}
