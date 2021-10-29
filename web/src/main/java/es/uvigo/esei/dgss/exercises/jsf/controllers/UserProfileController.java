package es.uvigo.esei.dgss.exercises.jsf.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.jsf.dto.PostDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Named("userProfileController")
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

	@Transactional
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
