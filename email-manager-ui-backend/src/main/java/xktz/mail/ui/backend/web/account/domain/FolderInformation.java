package xktz.mail.ui.backend.web.account.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import xktz.mail.account.MailFile;
import xktz.mail.account.MailFolder;

/**
 * Information of folder
 * @author XKTZ
 * @date 2022-07-02
 */
public class FolderInformation {

    @JsonProperty("folder")
    private MailFolder folder;

    @JsonProperty("folderPath")
    private String folderPath;

    @JsonProperty("children")
    private MailFile<?>[] children;

    public FolderInformation(MailFolder folder, MailFile<?>[] children) {
        this.folder = folder;
        this.folderPath = folder.fullName();
        this.children = children;
    }

    @JsonGetter("folder")
    public MailFolder getFolder() {
        return folder;
    }

    @JsonGetter("folderPath")
    public String getFolderPath() {
        return folderPath;
    }

    @JsonGetter("children")
    public MailFile<?>[] getChildren() {
        return children;
    }
}
