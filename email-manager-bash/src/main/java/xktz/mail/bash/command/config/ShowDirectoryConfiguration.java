package xktz.mail.bash.command.config;

import xktz.xkamework.annotation.Bean;
import xktz.xkamework.annotation.BeanConfiguration;

/**
 * @author XKTZ
 * @date 2022-06-20
 */
@BeanConfiguration
public class ShowDirectoryConfiguration {


    @Bean("showDirectoryIdLength")
    public int showDirectoryIdLength() {
        return 7;
    }

    @Bean("showDirectorySubjectLength")
    public int showDirectorySubjectLength() {
        return 45;
    }

    @Bean("showDirectoryFromLength")
    public int showDirectoryFromLength() {
        return 28;
    }

    @Bean("showDirectoryStateLength")
    public int showDirectoryStateLength() {
        return 10;
    }
}
