package xktz.mail.ui.backend.web.account.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mail account path
 *
 * @author XKTZ
 * @date 2022-07-01
 */
public class MailAccountPath {

    @JsonProperty("paths")
    private List<String> paths;

    @JsonSetter("paths")
    public List<String> getPaths() {
        return paths;
    }

    @JsonGetter("paths")
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String[] toArray() {
        return paths.toArray(String[]::new);
    }
}
