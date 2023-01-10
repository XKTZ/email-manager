package xktz.mail.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import xktz.mail.MailException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static xktz.mail.element.MailElement.MimeType.MAIL_TYPE_RELATED;

/**
 * Related elements
 *
 * @author XKTZ
 * @date 2022-06-15
 */
public class RelatedMailElement implements MailElement {

    /**
     * Image tag
     */
    private static final String IMG_TAG = "img";

    /**
     * Source attribute
     */
    private static final String SOURCE_ATTR = "src";

    /**
     * CID prefix
     */
    private static final String CID_PREFIX = "cid:";

    /**
     * Elements for images
     */
    private ImageMailElement[] images;

    /**
     * Images by cid
     */
    private Map<String, ImageMailElement> imagesByCID;

    /**
     * All elements
     */
    private MailElement[] elements;

    /**
     * Raw elements
     */
    private MailElement[] rawElements;

    public RelatedMailElement(Part part) throws MessagingException, IOException {
        var multipart = (Multipart) part.getContent();
        elements = new MailElement[multipart.getCount()];
        for (var i = 0; i < elements.length; i++) {
            elements[i] = MailElement.parse(multipart.getBodyPart(i));
        }
        rawElements = Arrays.stream(elements).map(element -> {
            if (element instanceof HTMLMailElement) {
                return (HTMLMailElement) ((HTMLMailElement) element).clone();
            } else {
                return element;
            }
        }).toArray(MailElement[]::new);
        images = Arrays.stream(elements)
                .filter(mailElement -> mailElement instanceof ImageMailElement)
                .map(ImageMailElement.class::cast)
                .toArray(ImageMailElement[]::new);
        imagesByCID = Arrays.stream(images)
                .collect(Collectors.toMap(ImageMailElement::getCid, Function.identity()));
        init();
    }

    /**
     * Initialize
     */
    private void init() {
        replaceWithImage();
    }

    /**
     * Replace all html elements's image tag src to images
     */
    private void replaceWithImage() {
        Arrays.stream(elements)
                .filter(mailElement -> mailElement instanceof HTMLMailElement)
                .map(HTMLMailElement.class::cast)
                .forEach(htmlMailElement -> htmlMailElement.operateHTML(document -> {
                    document.select(IMG_TAG).forEach(element -> {
                        if (element.attr(SOURCE_ATTR).startsWith(CID_PREFIX)) {
                            var cid = removeCIDPrefix(element.attr(SOURCE_ATTR));
                            if (!imagesByCID.containsKey(cid)) return;
                            var img = imagesByCID.get(cid);
                            // convert it to base 64
                            var data = new String(Base64.getEncoder().encode(img.getData()));
                            element.attr(SOURCE_ATTR, base64ImagePrefix(img.getEncoding()) + data);
                        }
                    });
                    return document;
                }, false));
    }

    /**
     * Elements in related
     *
     * @return elements
     */
    @JsonProperty("elements")
    public MailElement[] getElements() {
        return elements;
    }

    /**
     * Images in related
     *
     * @return images
     */
    public ImageMailElement[] images() {
        return images;
    }

    @Override
    public MailElementType getType() {
        return MailElementType.COMBINATION;
    }

    @Override
    public Part toPart() throws MessagingException {
        var part = new MimeBodyPart();
        var multiPart = new MimeMultipart();
        multiPart.setSubType(MAIL_TYPE_RELATED);
        for (var element: rawElements) {
            var partOn = element.toPart();
            if (partOn instanceof BodyPart) {
                multiPart.addBodyPart((BodyPart) partOn);
            }
        }
        part.setContent(multiPart);
        part.setHeader(HEADER_CONTENT_TYPE, MAIL_TYPE_RELATED);
        return part;
    }

    /**
     * Get the base 64 image prefix by providing encoding
     *
     * @param encoding encoding
     * @return prefix
     */
    private static String base64ImagePrefix(String encoding) {
        return MessageFormat.format("data:{0};base64, ", encoding);
    }

    /**
     * Remove cid prefix from raw cid src
     *
     * @param raw raw
     * @return removed prefix one
     */
    private static String removeCIDPrefix(String raw) {
        return raw.substring(CID_PREFIX.length());
    }

    /**
     * Get the related email element
     *
     * @param part part
     * @return element
     */
    public static RelatedMailElement of(Part part) {
        try {
            return new RelatedMailElement(part);
        } catch (MessagingException | IOException e) {
            throw new MailException(e);
        }
    }
}
