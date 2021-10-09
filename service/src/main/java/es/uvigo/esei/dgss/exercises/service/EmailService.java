package es.uvigo.esei.dgss.exercises.service;

import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.uvigo.esei.dgss.exercises.domain.User;

@Stateless
public class EmailService {
	@Resource
	private SessionContext sessionContext;

	@Resource
	private Session mailSession;

	@Asynchronous
	public Future<Boolean> sendEmail(User user, String subject, String body) {
		boolean emailSent = false;

		try {
			final MimeMessage m = new MimeMessage(mailSession);
			m.setFrom(InternetAddress.getLocalAddress(mailSession));
			m.setRecipient(RecipientType.TO, user.getEmail());
			m.setSubject(subject);
			m.setText(body, "UTF-8");

			Transport.send(m);

			// If we get here without throwing any exception, the message was
			// successfully sent
			emailSent = true;
		} catch (final MessagingException exc) {}

		return new AsyncResult<>(emailSent);
	}
}
