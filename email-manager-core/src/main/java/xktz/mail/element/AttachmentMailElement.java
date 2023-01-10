package xktz.mail.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import xktz.mail.MailException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Attachment element
 *
 * @author XKTZ
 * @date 2022-06-14
 */
public class AttachmentMailElement implements MailElement {

    /**
     * id
     */
    private String name;

    /**
     * data
     */
    private byte[] data;

    /**
     * Part
     */
    private Part part;

    /**
     * Attachment element
     *
     * @param part part
     * @throws MessagingException
     * @throws IOException
     */
    public AttachmentMailElement(Part part) throws MessagingException {
        this.name = part.getFileName();
        this.part = part;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("data")
    public byte[] getData() {
        try {
            return ((InputStream) part.getContent()).readAllBytes();
        } catch (IOException | MessagingException e) {
            throw new MailException(e);
        }
    }

    /**
     * Get attachment element by part
     *
     * @param part part
     * @return attachment element
     */
    public static AttachmentMailElement of(Part part) {
        try {
            return new AttachmentMailElement(part);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    @Override
    public MailElementType getType() {
        return MailElementType.ATTACHMENT;
    }

    @Override
    public Part toPart() throws MessagingException {
        var part = new MimeBodyPart();
        var bds = new ByteArrayDataSource(data, "AttName");
        part.setDataHandler(new DataHandler(bds));
        return part;
    }
}
