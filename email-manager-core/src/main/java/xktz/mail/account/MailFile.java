package xktz.mail.account;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * File in a mail folder. All things can appear in mail file is a MailFile
 *
 * @param <T> id type
 * @author XKTZ
 * @date 2022-06-14
 */
public interface MailFile<T> {
    /**
     * Name of mail folder file
     *
     * @return id
     */
    @JsonProperty("id")
    T id();

    /**
     * Displayed id of mail folder file
     *
     * @return displayed id
     */
    @JsonProperty("displayName")
    String displayName();

    /**
     * Type
     *
     * @return type
     */
    @JsonProperty("type")
    MailFileType getType();
}
