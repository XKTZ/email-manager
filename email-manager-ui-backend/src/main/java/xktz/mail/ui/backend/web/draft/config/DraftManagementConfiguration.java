package xktz.mail.ui.backend.web.draft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xktz.mail.ui.backend.error.exception.InitializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author XKTZ
 * @date 2022-07-08
 */
@Configuration
public class DraftManagementConfiguration {

    @Bean("draftFolderPath")
    public String draftFolderPath() {
        var path = Path.of("draft" + File.separator);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new InitializationException(e);
            }
        }
        return path.toAbsolutePath().toString();
    }
}
