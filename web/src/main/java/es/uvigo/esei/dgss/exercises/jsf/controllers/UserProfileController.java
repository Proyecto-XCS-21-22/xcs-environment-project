package es.uvigo.esei.dgss.exercises.jsf.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.jsf.dto.PostDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Named("userProfileController")
@Stateful
// I think this scope limits the lifetime of the object, even though it is stateful
// and mixing and matching annotations from different specifications is ugly.
// I need it to be a EJB because a transaction is needed to convert database entities
// to DTOs (more precisely in this case, view models). The service layer could do
// this conversion, but I don't think it's nice from a design perspective to couple
// the service layer with whatever auxiliary objects we use to show entities, so we
// do that here even though it doesn't seem like the specifications cover this
// usage of these annotations clearly
@RequestScoped
public class UserProfileController {
	@Inject
	private UserEJB users;

	private String login;
	private User user;
	private List<PostDTO> posts;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
		this.user = users.get(login);
		this.posts = users.getAuthoredPosts(login).stream().map(
			(Post p) -> PostDTO.of(p)
		).collect(Collectors.toList());
	}

	public User getUser() {
		return user;
	}

	public List<PostDTO> getPosts() {
		return posts;
	}
}
