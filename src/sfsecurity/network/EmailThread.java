package sfsecurity.network;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import sfsecurity.util.FileParser;
public class EmailThread extends Thread {
	static String credsFilename = "data/email_creds";
	static String addressList = "data/user_addresses";
	private String from;
	private String password;
	private ArrayList<String> to;
	private Collection<String> attachments;
	
	private String subject;
	private String emailText;
	private boolean readyToRun = false;
	
	public EmailThread(Collection<String> attachments) {
		this();
		this.attachments.addAll(attachments);
		this.subject = "Pictures!";
		this.emailText = "This email is to notify you that your pictures are here. \n"
				+        "Lorem Ipsum.  LOREM IPSUM!\n"
				+ 		 "Best Wishes\n"
				+        "	-Spencer";
	}
	
	public EmailThread() {
		readyToRun = false;
		ArrayList<String> creds = null;
		try {
			creds = FileParser.readFile(credsFilename);
		} catch (IOException e) {
			System.err.println("Error: Unable to read credentials file");
			e.printStackTrace();
			return;
		}
		if (creds.size() != 2) {
			System.err.println("ERROR: Email credentials MUST be only two lines: address and password");
			return;
		}
		from = creds.get(0);
		password = creds.get(1);
		try {
			to = FileParser.readFile(addressList);
		} catch (IOException e) {
			System.err.println("Error: Unable to read address list file");
			e.printStackTrace();
			return;
		}
		if (to.isEmpty()) {
			System.err.println("No addresses found");
			return;
		}
		attachments = new ArrayList<String>();
		
		readyToRun = true;
	}
	
	public void run() {
		if (this.readyToRun == false) {
			System.err.println("Unable to send email for some reason!");
			return;
		}
		
		
		
		
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
			
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			for (String addr : to) {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(addr));
			}
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();

			for (String attach : attachments) {
				File file = new File(attach);
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(file.getAbsolutePath());
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(file.getName());
				messageBodyPart.setDisposition(Part.ATTACHMENT);
				multipart.addBodyPart(messageBodyPart);
			}
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(emailText);
			textBodyPart.setDisposition(Part.INLINE);
			multipart.addBodyPart(textBodyPart);
			message.setContent(multipart);
//			System.out.println("sending...");
			
			Transport.send(message);
			
//			System.out.println("done");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		/** test this thread */
		ArrayList<String> myAttachments = new ArrayList<String>();
		myAttachments.add("data/picture.png");
		myAttachments.add("data/unnamed.jpg");
		EmailThread thread = new EmailThread(myAttachments);
		thread.start();
		System.out.println("Sending email....");
		thread.join();
		System.out.println("done");
		
	}
}
