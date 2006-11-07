package se.astrakan.ecore.util;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * This class provides functions to send mails.
 */
public class MailHandler  {

    public MailHandler() throws MailException {
        throw new MailException("don't use this class! It's just for testing WOFramework");
    }

    /**
     * This method composes the mails and delivers it to the destination address.
     * Note you have to set the mailHost in the system properties before it will work.
     *
    */
    public void composeMail(String from , String to , String subject, String body) throws SendFailedException,Exception{
        javax.mail.Session  session = javax.mail.Session.getDefaultInstance(System.getProperties(), null);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO,
                          InternetAddress.parse(to, true));
        if(subject != null )
            msg.setSubject(subject);
        msg.setText(body);
        msg.setSentDate(new Date());
        Transport.send(msg);

    }
}
