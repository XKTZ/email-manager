package xktz.mail.element;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import xktz.mail.MailException;

import java.io.IOException;

import static xktz.mail.element.MailElement.MimeType.MAIL_TYPE_MIXED;

/**
 * MailMessage element that is multiple part
 *
 * @author XKTZ
 * @date 2022-06-13
 */
public class MultipartMailElement implements MailElement {

    /**
     * MailMessage elements
     */
    private MailElement[] elements;

    /**
     * @param part multipart (parent)
     */
    public MultipartMailElement(Part part) throws IOException, MessagingException {
        Multipart multipart = (Multipart) part.getContent();
        elements = new MailElement[multipart.getCount()];
        for (int i = 0, num = multipart.getCount(); i < num; i++) {
            elements[i] = MailElement.parse(multipart.getBodyPart(i));
        }
    }

    public MailElement[] getElements() {
        return elements;
    }

    /**
     * Get multipart mail element
     *
     * @param part multipart (parent)
     * @return element
     */
    public static MultipartMailElement of(Part part) {
        try {
            return new MultipartMailElement(part);
        } catch (IOException | MessagingException e) {
            throw new MailException(e);
        }
    }

    @Override
    public final MailElementType getType() {
        return MailElementType.COLLECTION;
    }

    @Override
    public Part toPart() throws MessagingException {
        var part = new MimeBodyPart();
        var multi = new MimeMultipart();
        multi.setSubType(MAIL_TYPE_MIXED);
        for (var ele : elements) {
            var partOn = ele.toPart();
            if (partOn instanceof BodyPart) {
                multi.addBodyPart((BodyPart) partOn);
            }
        }
        part.setContent(multi);
        part.setHeader(HEADER_CONTENT_TYPE, MAIL_TYPE_MIXED);
        return part;
    }
}
