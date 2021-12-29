package es.uvigo.esei.dgss.exercises.rest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.rest.dto.UserDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@RunWith(Arquillian.class)
public class RestTest {
	@Deployment
	public static Archive<?> createAppDeployment() {
		// Internally, Thorntail bundles a WAR with the uberjar it creates, which is
		// deployed automagically on startup. We can't use @DefaultDeployment because
		// it misses the dependencies on other Maven modules
		return ShrinkWrap.create(WebArchive.class)
			.addClass(UsersResource.class)
			.addPackages(true, UserDTO.class.getPackage())
			.addPackages(true, UserEJB.class.getPackage())
			.addPackages(true, User.class.getPackage())
			// Transitive dependency of the User class. Not pulled in for some reason
			.addPackages(true, DigestUtils.class.getPackage())
			// This is not the same as adding as a manifest resource:
			// /WEB-INF/classes/META-INF/persistence.xml vs. /META-INF/persistence.xml
			.addAsResource("persistence.xml", "META-INF/persistence.xml")
			.addAsResource("project-test-db.yml")
			.addAsResource("project-defaults.yml")
			.addAsWebInfResource("web.xml")
			.addAsWebInfResource("jboss-web.xml");
	}

	@Test @InSequence(0)
	@UsingDataSet("users.xml")
	@Cleanup(phase = TestExecutionPhase.NONE)
	public void beforeGetUserTest() {}

	@Test @InSequence(1)
	@RunAsClient
	public void getUserTest() {
		final Response response = ClientBuilder.newClient()
			.target("http://localhost:8080/api/v1/users/pepe")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.header(HttpHeaders.AUTHORIZATION, "Basic cGVwZTpwZXBlcGFzcw==")
			.get();

		assertThat(response.getStatusInfo(), is(Status.OK));

		final UserDTO userData = response.readEntity(UserDTO.class);

		assertThat(userData.getLogin(), is("pepe"));
	}

	@Test @InSequence(2)
	@ShouldMatchDataSet("users.xml")
	public void afterGetUserTest() {}
}
