package utilities.email;

import com.sun.mail.util.BASE64DecoderStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;

import utilities.StormLog;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailMessage {

    private Message message;
    private String body;
    private ArrayList<String> attachments;

    public EmailMessage(Message msg) {
        message = msg;
        attachments = new ArrayList<>();
    }

    public Message getRawMessage() {
        return message;
    }

    public void markEmailAsRead() {
        StormLog.info("setting email as read", getClass());
        try {
            message.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void markEmailAsUnread() {
        StormLog.info("setting email as unread", getClass());
        try {
            message.setFlag(Flags.Flag.SEEN, false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmail() {
        StormLog.info("deleting email", getClass());
        try {
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String getSubject() {
        String subject = null;
        try {
            subject = message.getSubject();
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return subject;
    }

    public String getBody() {
        if (body == null) getTextFromMessage(message);
        return Jsoup.parse(body).text();
    }

    public ArrayList<String> getAttachments() {
        if (body == null) getTextFromMessage(message);
        return attachments;
    }

    public String[] getFrom() {
        String[] from = null;
        try {
            from = getAddressList(message.getFrom());
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return from;
    }

    public String[] getRecipients() {
        String[] recipients = null;
        try {
            recipients = getAddressList(message.getAllRecipients());
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return recipients;
    }

    public String[] getReplyTo() {
        String[] addresses = null;
        try {
            addresses = getAddressList(message.getReplyTo());
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return addresses;
    }

    public Date getSentDate() {
        Date sentDate = null;
        try {
            sentDate = message.getSentDate();
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return sentDate;
    }

    public Date getReceivedDate() {
        Date receivedDate = null;
        try {
            receivedDate = message.getReceivedDate();
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return receivedDate;
    }

    public int getMessageNumber() {
        return message.getMessageNumber();
    }

    public boolean bodyContains(String txt) {
        return getBody().contains(txt);
    }

    public boolean attachmentContains(String txt) {
        return getAttachments().stream().anyMatch(msg -> msg.contains(txt));
    }

    public boolean hasAttachment() {
        boolean attachment = false;
        try {
            attachment = message.isMimeType("multipart/*") &&
                    ((MimeMultipart) message.getContent()).getCount() > 1;
        } catch (MessagingException | IOException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return attachment;
    }

    private void getTextFromMessage(Message message) {
        StormLog.info("retrieving content for email", getClass());
        ArrayList<String> result = new ArrayList<>();
        try {
            if (message.isMimeType("text/plain")) {
                body = message.getContent().toString();
                result = attachments;
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();

                //top level loop to find body and attachments
                int count = mimeMultipart.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                    if (bodyPart.getContent() instanceof MimeMultipart) {
                        getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent(), result);
                    } else {
                        result.add(extractText(bodyPart));
                    }

                    if (body == null) {
                        body = result.stream().reduce((a, b) -> a + "\n" + b).orElse("");
                        result = attachments;
                    }
                }
            }
        } catch (IOException | MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        }
    }

    private ArrayList<String> getTextFromMimeMultipart(MimeMultipart mimeMultipart, ArrayList<String> content)  throws MessagingException, IOException{
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.getContent() instanceof MimeMultipart) {
                getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent(), content);
            } else {
                content.add(extractText(bodyPart));
            }
        }
        return content;
    }

    private String extractText(BodyPart bodyPart) throws MessagingException, IOException {
        String text;
        if (bodyPart.isMimeType("text/plain")) {
            text = bodyPart.getContent().toString();
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            text = Jsoup.parse(html).text();
        } else if (bodyPart.getContent() instanceof BASE64DecoderStream) {
            try (PDDocument document = PDDocument.load((BASE64DecoderStream) bodyPart.getContent())) {
                PDFTextStripper stripper = new PDFTextStripper();
                text = stripper.getText(document);
            }
        } else {
            StormLog.error("Email parser does not know how to handle type " + bodyPart.getContentType(), getClass());
            text = null;
        }
        return text;
    }

    private String[] getAddressList(Address[] addresses) {
        String[] addressString = new String[addresses.length];
        //regex pattern to find an email in a string
        Pattern pattern = Pattern.compile(".*?([\\w|\\d]+@[\\w|\\d]+\\.\\w+).*");
        for (int i = 0; i < addresses.length; ++i) {
            String address = addresses[i].toString();
            Matcher matcher = pattern.matcher(address);
            matcher.find();
            addressString[i] = matcher.group(1);
        }
        return addressString;
    }
}
