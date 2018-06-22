package utilities.email;
import utilities.StormLog;
import utilities.StormProperties;
import utilities.StormUtils;

import javax.mail.*;

import java.util.Arrays;
import java.util.Properties;

//this is setup to work with gmail
public class EmailApi {

    //enable imap in settings -> https://support.google.com/mail/answer/7126229?hl=en
    //allow less secure apps -> https://myaccount.google.com/lesssecureapps?pli=1

    private static EmailApi instance;

    private Folder inbox;
    private Store store;

    public static EmailApi emailClient() {
        if (instance == null) {
            instance = new EmailApi();
        }
        return instance;
    }

    private EmailApi() {
        String email = StormProperties.getProperty("realEmailAddress");
        String password = StormProperties.getProperty("realEmailPassword");
        String host = StormProperties.getProperty("emailImapHost");
        Properties props = new Properties();
        try{
            StormLog.info("connecting to email account " + email, getClass());
            //set email protocol to IMAP
            props.put("mail.store.protocol", "imaps");
            //set up the session
            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            //Connect to your email account
            store.connect(host, email, password);

            //Get reference to your INBOX
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            inbox.close(true);
            store.close();
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        }
    }

    public EmailMessage[] getEmails(int from, int to) {
        Message[] messages = null;
        StormLog.info("Getting emails from " + from + " to " + to, getClass());
        try {
            messages = inbox.getMessages(from, to);
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        }
        return convertToEmailMessage(messages);
    }

    public int getEmailCount() {
        int count = 0;
        try {
            count = inbox.getMessageCount();
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return count;
    }

    public boolean waitForEmail() {
        final int count = getEmailCount();
        StormLog.info("waiting for new email to arrive", getClass());
        return StormUtils.waitForTrue(()->getEmailCount() > count, 20);
    }

    public boolean waitForEmail(String subject) {
        final int count = getEmailCount();
        StormLog.info("waiting for new email with subject: " + subject, getClass());
        return StormUtils.waitForTrue(()->getEmailCount() > count &&
                getMostRecentEmail().getSubject().equals(subject), 20);
    }

    public EmailMessage getEmail(int index) {
        StormLog.info("Getting email " + index, getClass());
        Message message = null;
        try {
            message = inbox.getMessage(index);
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return new EmailMessage(message);
    }

    public EmailMessage getEmail(String subject) {
        StormLog.info("Getting email " + subject, getClass());

        //to find the most recent email with that subject we will look at the most recent 100
        EmailMessage[] recentEmails = getRecentEmails(100);
        for (int i = recentEmails.length - 1; i >= 0; --i) {
            if (recentEmails[i].getSubject().equals(subject)) {
                return recentEmails[i];
            }
        }

        StormLog.warn("could not find email '" + subject + "' in the last 100 emails", getClass());
        return null;
    }

    public EmailMessage[] getRecentEmails() {
        return getRecentEmails(15);
    }

    public EmailMessage[] getRecentEmails(int recent) {
        Message[] messages = null;
        //by subtracting one it will return this many, otherwise it is one too many
        StormLog.info("Getting the most recent " +recent-- + " emails", getClass());
        try {
            int count = inbox.getMessageCount();
            messages = inbox.getMessages(count - recent, count);
        } catch (MessagingException e) {
            StormLog.error(e, getClass());
            e.printStackTrace();
        } 
        return convertToEmailMessage(messages);
    }

    public EmailMessage getMostRecentEmail() {
        return getEmail(getEmailCount());
    }

    private EmailMessage[] convertToEmailMessage(Message[] msgs) {
        return Arrays.stream(msgs).map(EmailMessage::new).toArray(EmailMessage[]::new);
    }
}