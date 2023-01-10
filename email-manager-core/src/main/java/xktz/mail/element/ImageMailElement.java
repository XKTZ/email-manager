package xktz.mail.element;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import xktz.mail.MailException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Image element
 *
 * @author XKTZ
 * @date 2022-06-14
 */
public class ImageMailElement implements MailElement {

    /**
     * Image prefix
     */
    private static final String IMAGE_PREFIX = "IMAGE/";

    /**
     * Image prefix
     */
    private static final String IMAGE_PNG = "IMAGE/PNG";

    /**
     * Name prefix
     */
    private static final String NAME_PREFIX = "id";

    /**
     * Name of string
     */
    private String name;

    /**
     * Encoding method
     */
    private String encoding;

    /**
     * Data
     */
    private byte[] data;

    /**
     * Cid
     */
    private String cid;

    /**
     * @param part part
     * @throws MessagingException
     * @throws IOException
     */
    public ImageMailElement(Part part) throws MessagingException, IOException {
        this.name = Arrays.stream(part.getContentType().split(";"))
                .filter(s -> s.startsWith(NAME_PREFIX))
                .findAny()
                .orElse("");
        this.encoding = Arrays.stream(part.getContentType().split(";"))
                .filter(s -> s.startsWith(IMAGE_PREFIX))
                .findAny()
                .orElse(IMAGE_PNG);
        this.cid = ((MimeBodyPart) part).getContentID();
        // cid has <>, so strip
        this.cid = cid.substring(1, cid.length() - 1);

        try (var s = (InputStream) part.getContent()) {
            this.data = s.readAllBytes();
        }
    }

    public String getName() {
        return name;
    }

    public String getEncoding() {
        return encoding;
    }

    public byte[] getData() {
        return data;
    }

    public String getCid() {
        return cid;
    }

    /**
     * Get attachment element by part
     *
     * @param part part
     * @return attachment element
     */
    public static ImageMailElement of(Part part) {
        try {
            return new ImageMailElement(part);
        } catch (MessagingException | IOException e) {
            throw new MailException(e);
        }
    }

    @Override
    public MailElementType getType() {
        return MailElementType.IMAGE;
    }

    @Override
    public Part toPart() throws MessagingException {
        var part = new MimeBodyPart();
        part.setContentID(cid);
        var bds = new ByteArrayDataSource(data, encoding);
        part.setDataHandler(new DataHandler(bds));
        part.setFileName(name);
        part.setContent(HEADER_CONTENT_TYPE, encoding);
        return part;
    }
}
