package xktz.mail.bash.config;

import xktz.mail.bash.error.MailBashException;
import xktz.xkamework.annotation.Bean;
import xktz.xkamework.annotation.BeanConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;

/**
 * Configuration for bash
 *
 * @author XKTZ
 * @date 2022-06-16
 */
@BeanConfiguration
public class EmailManagerConfiguration {

    /**
     * location for config file
     */
    private static final String CONF_FILE_LOCATION = "./mail.conf";

    /**
     * Configuration
     *
     * @return config
     */
    @Bean("emailConfiguration")
    public Properties configuration() {
        Path pathConfig = Path.of("./mail.conf");
        if (!Files.exists(pathConfig)) {
            throw new MailBashException(new FileNotFoundException(pathConfig.toAbsolutePath().toString()));
        }
        Properties properties = new Properties();
        try (var stream = Files.newInputStream(pathConfig)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new MailBashException(e);
        }
        return properties;
    }

    /**
     * Terminal printer
     *
     * @return terminal printer
     */
    @Bean("terminalPrinter")
    public PrintStream terminalPrinter() {
        return System.out;
    }

    /**
     * Terminal error printer
     *
     * @return error printer
     */
    @Bean("terminalErrorPrinter")
    public PrintStream errorPrinter() {
        return System.err;
    }


    /**
     * Terminal scanner
     *
     * @return scanner
     */
    @Bean("terminalScanner")
    public Scanner terminalScanner() {
        return new Scanner(System.in);
    }
}
