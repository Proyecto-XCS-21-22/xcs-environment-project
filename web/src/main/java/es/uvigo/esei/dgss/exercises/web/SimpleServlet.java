package es.uvigo.esei.dgss.exercises.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import es.uvigo.esei.dgss.exercises.domain.User;

@WebServlet("/SimpleServlet")
public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private Facade facade;

	@Resource
	private UserTransaction transaction;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();

		writer.println("<html>");
		writer.println("<head>");
		writer.println("<meta charset='utf-8'>");
		writer.println("<style>label { display: block; } button { display: block; }</style>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<h1>Facade tests</h1>");

		writer.println(
			"<form method='POST'>"
			+ "<button type='submit' name='task' value='1'>Task 1. Create User</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='22_login1'>Sender login:</label>"
			+ "<input id='22_login1' name='login1' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='22_login2'>Receiver login:</label>"
			+ "<input id='22_login2' name='login2' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='2'>Task 2. Create Friendship</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='23_login'>User login:</label>"
			+ "<input id='23_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='3'>Task 3. Get All Friends</button>" +
			"</form>"
		);

		writer.println("</body>");
		writer.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();

		writer.println("<html><body>");

		switch (req.getParameter("task")) {
			case "1":
				task1(req, resp, writer);
				break;
			case "2":
				task2(req, resp, writer);
				break;
			case "3":
				task3(req, resp, writer);
				break;
		}

		writer.println("</body></html>");
	}

	private void task1(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			transaction.begin();

			// Task 2.1
			User u = facade.addUser(UUID.randomUUID().toString(), "name", "password", new byte[] {});
			writer.println("<p>User " + u.getLogin() + " created successfully</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task2(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login1 = req.getParameter("login1");
			String login2 = req.getParameter("login2");

			transaction.begin();

			// Task 2.2
			User u = facade.getUser(login1);
			User v = facade.getUser(login2);
			facade.addFriendship(u, v);

			writer.println("<p>Friendship from " + login1 + " to " + login2 + " created successfully</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task3(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");

			transaction.begin();

			// Task 2.3
			String friendLogins = facade.getUser(login)
				.getFriends().stream().map(
					(User f) -> f.getLogin()
				).collect(Collectors.joining(", "));

			writer.println("<p>Friends for user " + login + ": " + friendLogins + "</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}
}
