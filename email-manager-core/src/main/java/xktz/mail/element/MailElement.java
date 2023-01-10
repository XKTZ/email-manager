package xktz.mail.element;

import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import xktz.mail.MailException;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import static xktz.mail.element.MailElement.MimeType.*;

/**
 * An element in mail
 *
 * @author XKTZ
 * @date 2022-06-13
 */
public interface MailElement {

    /**
     * If there exist "id" in mail element, then it is attachment
     */
    String PLAIN_TEXT_ATTACHMENT_KEY_WORD = "id";

    /**
     * Content type header
     */
    String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * All parsers
     */
    PartParser[] PARSERS = {
            PartParser.mapOf(PartParser.mimeTypeIs(MAIL_TYPE_TEXT)
                            .and(PartParser.mimeTypeIs(MAIL_TYPE_HTML).negate())
                            .and(PartParser.contentTypeContains(PLAIN_TEXT_ATTACHMENT_KEY_WORD).negate()),
                    PartParser.READ_STRING_IN_PART,
                    HTMLMailElement::textOf
            ),
            PartParser.mapOf(PartParser.mimeTypeIs(MAIL_TYPE_HTML),
                    PartParser.READ_STRING_IN_PART,
                    HTMLMailElement::htmlOf
            ),
            PartParser.of(PartParser.mimeTypeIs(MAIL_TYPE_ALTERNATIVE), HTMLMailElement::of),
            PartParser.of(PartParser.mimeTypeIs(MAIL_TYPE_RELATED), RelatedMailElement::of),
            PartParser.of(PartParser.mimeTypeIs(MAIL_TYPE_MIXED),
                    MultipartMailElement::of),
            PartParser.of(PartParser.mimeTypeIs(MAIL_TYPE_IMAGE), ImageMailElement::of),
            PartParser.of(PartParser.IS_ATTACHMENT_DISPOSITION.
                            or(PartParser.mimeTypeIs(MAIL_TYPE_IMAGE).negate().and(PartParser.contentTypeContains(PLAIN_TEXT_ATTACHMENT_KEY_WORD))),
                    AttachmentMailElement::of),
            PartParser.of(PartParser.mimeTypeIs(MAIL_TYPE_RFC_822), PartParser.content())
    };

    /**
     * Get the type of element
     *
     * @return type
     */
    MailElementType getType();

    /**
     * Convert the mail element back to part
     *
     * @return
     */
    Part toPart() throws MessagingException;

    /**
     * Parse a part into element
     *
     * @return element
     */
    static MailElement parse(Part part) {
        for (var parser : PARSERS) {
            if (parser.check(part)) {
                return parser.parse(part);
            }
        }
        return null;
    }

    interface MimeType {

        /**
         * MailMessage type multipart
         */
        String MAIL_TYPE_MIXED = "MULTIPART/MIXED";

        /**
         * MailMessage type multipart
         */
        String MAIL_TYPE_RELATED = "MULTIPART/RELATED";

        /**
         * MailMessage type alternative
         */
        String MAIL_TYPE_ALTERNATIVE = "MULTIPART/ALTERNATIVE";

        /**
         * MailMessage plain text
         */
        String MAIL_TYPE_PLAIN_TEXT = "TEXT/PLAIN";

        /**
         * MailMessage html
         */
        String MAIL_TYPE_HTML = "TEXT/HTML";

        /**
         * MailMessage all text
         */
        String MAIL_TYPE_TEXT = "TEXT/*";

        /**
         * MailMessage type image
         */
        String MAIL_TYPE_IMAGE = "IMAGE/*";

        /**
         * MailMessage type rfc822
         */
        String MAIL_TYPE_RFC_822 = "MESSAGE/RFC822";
    }

    /**
     * Parser parse the element
     */
    interface PartParser {

        /**
         * Function read string from part
         */
        Function<Part, String> READ_STRING_IN_PART = part -> {
            try {
                return part.getContent().toString();
            } catch (IOException | MessagingException e) {
                throw new MailException(e);
            }
        };

        /**
         * Function get the content type
         */
        Function<Part, String> CONTENT_TYPE = part -> {
            try {
                return part.getContentType();
            } catch (MessagingException e) {
                throw new MailException(e);
            }
        };

        /**
         * Function check if it is attachment disposition
         */
        Predicate<Part> IS_ATTACHMENT_DISPOSITION = part -> {
            try {
                return Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition());
            } catch (MessagingException e) {
                throw new MailException(e);
            }
        };

        /**
         * Check if this part is valid or not
         *
         * @return valid or not
         */
        boolean check(Part part);

        /**
         * Parse element
         *
         * @param part part
         * @return parsed element
         */
        MailElement parse(Part part);

        /**
         * Get parser by give checker and parser function
         *
         * @param check checker
         * @param parse parser
         * @return parser
         */
        static PartParser of(Predicate<Part> check, Function<Part, MailElement> parse) {
            return new PartParser() {
                @Override
                public boolean check(Part part) {
                    return check.test(part);
                }

                @Override
                public MailElement parse(Part part) {
                    return parse.apply(part);
                }
            };
        }

        /**
         * Get parser by linked conversion
         *
         * @param converter parser
         * @return parser
         */
        static <T> PartParser mapOf(Predicate<Part> check, Function<Part, T> converter, Function<T, MailElement> parse) {
            return of(check, part -> parse.apply(converter.apply(part)));
        }

        /**
         * Checker by mime type
         *
         * @param mimeType mime type
         * @return checker
         */
        static Predicate<Part> mimeTypeIs(String mimeType) {
            return part -> {
                try {
                    return part.isMimeType(mimeType);
                } catch (MessagingException e) {
                    throw new MailException(e);
                }
            };
        }

        /**
         * Checker by content type
         *
         * @param contentType content type
         * @return checker
         */
        static Predicate<Part> contentTypeContains(String contentType) {
            return part -> CONTENT_TYPE.apply(part).contains(contentType);
        }


        /**
         * Function get the content type
         */
        static <T> Function<Part, T> content() {
            return p -> {
                try {
                    return (T) p.getContent();
                } catch (MessagingException | IOException e) {
                    throw new MailException(e);
                }
            };
        }
    }
}