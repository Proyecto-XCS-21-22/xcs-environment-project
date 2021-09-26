package es.uvigo.esei.dgss.exercises.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

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
		writer.println("<body>");
		writer.println("<h1>Facade tests</h1>");

		String[] tasks = { "Create User", "Create Friendship" };
		for (int i = 1; i <= tasks.length; ++i) {
			writer.println(
				"<form method='POST'>"
				+ "<button type='submit' name='task' value='" + i + "'>Task " + i + ". " + tasks[i - 1] + "</button>" +
				"</form>"
			);
		}

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
			transaction.begin();

			// Task 2.2
			User u = facade.addUser(UUID.randomUUID().toString(), "name", "password", new byte[] {});
			User v = facade.addUser(UUID.randomUUID().toString(), "name", "password", new byte[] {});

			facade.addFriendship(u, v);

			writer.println("<p>Friendship from " + u.getLogin() + " to " + v.getLogin() + " created successfully</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}
}
