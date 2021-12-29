package es.uvigo.esei.dgss.exercises.domain;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

public class UserTest {
	private static final String LOGIN = "login";
	private static final String NAME = "name";
	private static final InternetAddress EMAIL;
	private static final String PASSWORD = "p@ssw0rd";
	private static final String HASHED_PASSWORD = "0f359740bd1cda994f8b55330c86d845";

	static {
		try {
			EMAIL = new InternetAddress("user@host");
		} catch (final AddressException e) {
			throw new Error(e);
		}
	}

	@Test
	public void testCreation() {
		final User user = new User(LOGIN, NAME, EMAIL, PASSWORD);

		assertThat(user.getLogin(), is(LOGIN));
		assertThat(user.getName(), is(NAME));
		assertThat(user.getEmail(), is(EMAIL));
		assertThat(user.getPassword(), is(HASHED_PASSWORD));
	}
}
