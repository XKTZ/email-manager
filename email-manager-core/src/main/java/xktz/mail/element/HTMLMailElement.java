package xktz.mail.element;

import jakarta.activation.DataHandler;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import xktz.mail.MailException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

import static xktz.mail.element.MailElement.MimeType.MAIL_TYPE_HTML;

/**
 * HTML Element
 *
 * @author XKTZ
 * @date 2022-06-13
 */
public class HTMLMailElement implements MailElement {


    /**
     * HTML of string
     */
    private Document html;

    /**
     * Text of string
     */
    private String text;

    public HTMLMailElement(Document html) {
        this.html = html;
        this.text = html.wholeText();
    }

    public HTMLMailElement(String text) {
        this.text = text;
        this.html = plainTextToHTML(text);
    }

    public HTMLMailElement(Document html, String text) {
        this.html = html;
        this.text = text;
    }

    /**
     * Operate the html node
     *
     * @param func    function
     * @param refresh refresh (with text) after function created or not
     */
    public void operateHTML(Function<Document, Document> func, boolean refresh) {
        this.html = func.apply(this.html);
        if (refresh) {
            this.text = this.html.wholeText();
        }
    }

    /**
     * get the html of element
     *
     * @return html
     */
    public String getHTML() {
        return html.body().html();
    }

    /**
     * Set the html of element
     *
     * @param html html
     */
    public void setHTML(String html) {
        this.html = Jsoup.parseBodyFragment(html);
    }

    /**
     * Get the text of element
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of element
     *
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public final MailElementType getType() {
        return MailElementType.HTML;
    }

    @Override
    public Part toPart() throws MessagingException {
        var part = new MimeBodyPart();
        part.setContent(html.body().html(), "text/html");
        part.setHeader(HEADER_CONTENT_TYPE, MAIL_TYPE_HTML);
        return part;
    }

    /**
     * Get a html mail element by text
     *
     * @return the element
     */
    public static HTMLMailElement textOf(String text) {
        return new HTMLMailElement(text);
    }

    /**
     * Get a html mail element by html
     *
     * @param html html
     * @return the element
     */
    public static HTMLMailElement htmlOf(String html) {
        return new HTMLMailElement(Jsoup.parseBodyFragment(html));
    }

    /**
     * Get a html mail element by both html and text
     *
     * @param html html
     * @param text text
     * @return the element
     */
    public static HTMLMailElement of(String html, String text) {
        return new HTMLMailElement(Jsoup.parseBodyFragment(html), text);
    }

    /**
     * Get HTML MailMessage Element from  multiple part
     *
     * @param part multipart element (parent)
     * @return HTML MailMessage Element
     */
    public static HTMLMailElement of(Part part) {
        try {
            var multipart = (Multipart) part.getContent();
            String text = "";
            Document html = null;
            for (int i = 0, count = multipart.getCount(); i < count; i++) {
                var partBody = multipart.getBodyPart(i);
                if (partBody.isMimeType(MimeType.MAIL_TYPE_PLAIN_TEXT)) {
                    text = partBody.getContent().toString();
                } else if (partBody.isMimeType(MAIL_TYPE_HTML)) {
                    var rawHtml = partBody.getContent().toString();
                    if (rawHtml.endsWith("\r\n")) {
                        rawHtml = rawHtml.substring(0, rawHtml.length() - 2);
                    }
                    html = Jsoup.parseBodyFragment(rawHtml);
                }
            }
            if (html == null) {
                return new HTMLMailElement(text);
            } else if (text == null) {
                return new HTMLMailElement(html);
            } else {
                return new HTMLMailElement(html, text);
            }
        } catch (IOException | MessagingException e) {
            throw new MailException(e);
        }
    }

    @Override
    protected Object clone() {
        return HTMLMailElement.of(html.body().html(), text);
    }

    /**
     * Transfer plain text to html (transfer '\n' to divs)
     *
     * @param text text
     * @return html
     */
    private static Document plainTextToHTML(String text) {
        return Jsoup.parseBodyFragment(Arrays.stream(text.split("\\r?\\n"))
                .map(s -> s.replace("&", "&amp;"))
                .map(s -> s.replace("<", "&lt;"))
                .map(s -> s.replace(">", "&gt;"))
                .collect(StringBuilder::new, (sb, line) -> {
                    sb.append("<div>").append(line).append("</div>");
                }, StringBuilder::append).toString());
    }
}
