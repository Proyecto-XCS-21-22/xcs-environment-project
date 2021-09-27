package es.uvigo.esei.dgss.exercises.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.transaction.UserTransaction;

import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;

@WebServlet("/SimpleServlet")
@MultipartConfig
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
			"<form method='POST' enctype='multipart/form-data'>"
			+ "<label for='1_login'>Login:</label>"
			+ "<input id='1_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='1_name'>Name:</label>"
			+ "<input id='1_name' name='name' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='1_password'>Password:</label>"
			+ "<input id='1_password' name='password' size='64' minlength='8' maxlength='64' required>"
			+ "<label for='1_picture'>Picture:</label>"
			+ "<input id='1_picture' name='picture' type='file' accept='image/*'>"
			+ "<button type='submit' name='task' value='1'>Task 1. Create User</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='2_login1'>Sender login:</label>"
			+ "<input id='2_login1' name='login1' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='2_login2'>Receiver login:</label>"
			+ "<input id='2_login2' name='login2' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='2'>Task 2. Create Friendship</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='3_login'>User login:</label>"
			+ "<input id='3_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='3'>Task 3. Get All Friends</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='4_login'>User login:</label>"
			+ "<input id='4_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='4'>Task 4. Get Posts of Friends</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='5_login'>User login:</label>"
			+ "<input id='5_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='5_date'>Date:</label>"
			+ "<input id='5_date' name='date' type='date' required>"
			+ "<button type='submit' name='task' value='5'>Task 5. Get Posts Commented by Friends after Date</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='6_login'>User login:</label>"
			+ "<input id='6_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<label for='6_postId'>Post ID:</label>"
			+ "<input id='6_postId' name='postId' type='number' required>"
			+ "<button type='submit' name='task' value='6'>Task 6. Get Friends of User Who Like a Post</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='7_login'>User login:</label>"
			+ "<input id='7_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='7'>Task 7. Get Pictures Liked by User</button>" +
			"</form>"
		);

		writer.println(
			"<form method='POST'>"
			+ "<label for='8_login'>User login:</label>"
			+ "<input id='8_login' name='login' size='64' minlength='1' maxlength='64' required>"
			+ "<button type='submit' name='task' value='8'>Task 8. Get Potential Friends</button>" +
			"</form>"
		);

		writer.println("</body>");
		writer.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();

		writer.println("<html><head><meta charset='utf-8'></head><body>");

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
			case "4":
				task4(req, resp, writer);
				break;
			case "5":
				task5(req, resp, writer);
				break;
			case "6":
				task6(req, resp, writer);
				break;
			case "7":
				task7(req, resp, writer);
				break;
			case "8":
				task8(req, resp, writer);
				break;
		}

		writer.println("</body></html>");
	}

	private void task1(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");
			String name = req.getParameter("name");
			String password = req.getParameter("password");

			Part picture = req.getPart("picture");
			byte[] pictureData;

			if (picture != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(
					(int) Math.min(picture.getSize(), Integer.MAX_VALUE)
				);

				transferTo(picture.getInputStream(), bos);
				pictureData = bos.toByteArray();
			} else {
				pictureData = null;
			}

			transaction.begin();

			// Task 2.1
			User u = facade.addUser(login, name, password, pictureData);
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
				.getFriends().stream()
				.map(
					(User u) -> u.getLogin()
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

	private void task4(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");

			transaction.begin();

			// Task 2.4
			String postIds = facade.getUser(login)
				.getFriends().stream()
				.flatMap(
					(User u) -> u.getPosts().stream()
				)
				.map(
					(Post p) -> Long.toString(p.getId())
				).collect(Collectors.joining(", "));

			writer.println("<p>Posts for friends of " + login + ": " + postIds + "</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task5(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");
			String dateString = req.getParameter("date");
			Date date = Date.from(
				DateTimeFormatter.ISO_LOCAL_DATE.parse(
					dateString, LocalDate::from
				)
				.atTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC))
				.toInstant()
			);

			transaction.begin();

			// Task 2.5
			String postIds = facade.getPostsCommentedByFriendsAfterDate(login, date).stream()
				.map(
					(Post p) -> Long.toString(p.getId())
				).collect(Collectors.joining(", "));

			writer.println("<p>Posts commeted by friends of " + login + " after " + dateString + ": " + postIds + "</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task6(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");
			long postId = Long.parseLong(req.getParameter("postId"));

			transaction.begin();

			// Task 2.6
			String friendLogins = facade.getFriendsWhoLikePost(login, postId).stream()
				.map(
					(User u) -> u.getLogin()
				).collect(Collectors.joining(", "));

			writer.println("<p>Friends of " + login + " who like post ID " + postId + ": " + friendLogins + "</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task7(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");

			transaction.begin();

			// Task 2.7
			String pictureIds = facade.getLikedPictures(login).stream()
				.map(
					(Photo p) -> Long.toString(p.getId())
				).collect(Collectors.joining(", "));

			writer.println("<p>Pictures liked by " + login + " : " + pictureIds + "</p>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private void task8(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) throws ServletException {
		try {
			String login = req.getParameter("login");

			transaction.begin();

			// Task 2.8
			User user = facade.getUser(login);
			Collection<User> friends = user.getFriends();

			writer.print("<p>Potential friends for " + login + ":</p><ul>");

			// Use loops instead of the Stream API for performance.
			// The database will return more rows than strictly needed to fulfill
			// this request, but doing so at the application level can be just fine
			// in some cases for several reasons:
			// - If the database is on the same host, there is no concern for
			//   the additional network traffic.
			// - Changing the application logic is easier than the database, or
			//   JPQL queries themselves, which are not checked at compile time
			//   for correctness.
			// - Depending on the query plan and indexes the RDBMS uses, doing this
			//   filtering at the application level may be as efficient in terms of
			//   algorithmic complexity (although if performance is a concern the
			//   RDBMS can probably be set up to do a better job than the application).
			for (User friend : friends) {
				Collection<User> friendsOfFriend = friend.getFriends();

				Iterator<User> potentialFriendIter = friendsOfFriend.iterator();
				while (potentialFriendIter.hasNext()) {
					User potentialFriend = potentialFriendIter.next();

					if (
						// If A is friend of B and C, and B is friend of C, C will
						// be in the set, despite them already being A's friend.
						// We only want to show new friends
						!friends.contains(potentialFriend) &&
						// If A is friend of B, then B is friend of A, and A will be
						// in the set. However, we do not want to befriend ourselves
						!user.equals(potentialFriend)
					) {
						writer.println("<li>" + potentialFriend.getLogin() + "</li>");
					}
				}
			}

			writer.println("</ul>");

			writer.println("<a href='SimpleServlet'>Go to menu</a>");

			transaction.commit();
		} catch (Exception exc) {
			// XXX: because this is not a production environment, wrapping this in a checked
			// exception is a concise way of bringing attention to any error that happens
			throw new ServletException(exc);
		}
	}

	private static final void transferTo(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[4096];
		int bytesRead;

		while ((bytesRead = is.read(buf)) > 0) {
			os.write(buf, 0, bytesRead);
		}
	}
}
