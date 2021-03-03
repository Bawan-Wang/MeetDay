package jsp;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class MailRelated {
	
	private String authuser = "sylapp7ntust@gmail.com"; 
	private String authpwd = "Ntust1357924680";
	private String hostname = "smtp.gmail.com";
	private int smtpport = 465;
	private String Charset = "UTF-8";
	
    static final String FROM = "ycw517@gmail.com";   // Replace with your "From" address. This address must be verified.
    //static final String TO = "yuki.wu@aver.com";  // Replace with a "To" address. If your account is still in the 
                                                       // sandbox, this address must be verified.
    
    static final String BODY = "This email was sent through the Amazon SES SMTP interface by using Java.";
    static final String SUBJECT = "Amazon SES test (SMTP interface accessed using Java)";
    
    // Supply your SMTP credentials below. Note that your SMTP credentials are different from your AWS credentials.
    static final String SMTP_USERNAME = "AKIAJUKZULPVUAUEKBZA";  // Replace with your SMTP username.
    static final String SMTP_PASSWORD = "Ag13dHuIOXoFgYcIfHKcC/sS/jly0zI6xKjywEGq03Ab";  // Replace with your SMTP password.
    
    // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
    static final String HOST = "email-smtp.us-west-2.amazonaws.com";    
    
    // Port we will connect to on the Amazon SES SMTP endpoint. We are choosing port 25 because we will use
    // STARTTLS to encrypt the connection.
    static final int PORT = 25;
	
	public MailRelated() {
		 //test = new JDBCMysql(); 
	 }
	
	public void Send_AWS_SES_Mail(String Sub, String Body, String TO) throws AddressException, MessagingException{
		// Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", PORT); 
    	
    	// Set properties indicating that we want to use STARTTLS to encrypt the connection.
    	// The SMTP session will begin on an unencrypted connection, and then the client
        // will issue a STARTTLS command to upgrade to an encrypted connection.
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.starttls.required", "true");

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        msg.setSubject(Sub);
        msg.setContent(Body,"text/html; charset=utf-8");
            
        // Create a transport.        
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();        	
        }
	}
	
	public boolean Mail_Reg(String name, String pass, String num) throws AddressException, MessagingException{
		String subject="Welcome to PreviewGuide";
		String message = "<html><head><title>Register</title></head><body>Thank you for your registration <br>"
				+ "Please!! Click Below Link to activate your account:" +
				"<br> <br> http://128.199.208.170:8080/Test4Demo/activate.jsp?name="+name+"&num="+num+"&mode=1"+"</body></html>"; 
		
		Email email = new HtmlEmail(); 
		email.setHostName(hostname);
		email.setSmtpPort(smtpport); 
		email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
		email.setDebug(true);
		email.setSSL(true);
		email.setSslSmtpPort(Integer.toString(smtpport));
		email.setCharset(Charset);
		email.setSubject(subject);
		try {
		    email.setFrom(authuser, "PreviewGuide Center");
		    email.setMsg(message); 
		    email.addTo(name, "Dear Member");
		    email.send();
		    Send_AWS_SES_Mail(subject, message, name);
		    System.out.println("Email Sent"); 
		    return true;
		} catch (EmailException e) {
			System.out.println("fail"); 
		    e.printStackTrace();
		    return false;
		}	
	}
	
	@SuppressWarnings("deprecation")
	public boolean Mail_ForgetPwd(String name, String pass) throws AddressException, MessagingException{
		String subject="PreviewGuide Send Password";
		String message = "<html><head><title>SendPass</title></head><body>Hi, "+name+" Your Password: "+pass+"</body></html>"; 
		System.out.println(pass); 
		Email email = new HtmlEmail(); 
		email.setHostName(hostname);
		email.setSmtpPort(smtpport); 
		email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
		email.setDebug(true);
		email.setSSL(true);
		email.setSslSmtpPort(Integer.toString(smtpport));
		email.setCharset(Charset);
		email.setSubject(subject);
		try {
		    email.setFrom(authuser, "Preview Guide Center");
		    email.setMsg(message); 
		    email.addTo(name, "Dear Member");
		    //email.send();
		    Send_AWS_SES_Mail(subject, message, name);
		    System.out.println("Email Sent"); 
		    return true;    
		} catch (EmailException e) {
			System.out.println("fail"); 
		    e.printStackTrace();
		    return false;
		}	

	}
}
