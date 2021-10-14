package es.uvigo.esei.dgss.exercises.rest.dto;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.User;

public class UserDTO {
	private String login;
	private String name;
	private String email;
	private byte[] picture;
	private String posts;

	public static UserDTO of(User u, UriBuilder baseUri) {
		final UserDTO dto = new UserDTO();

		dto.setLogin(u.getLogin());
		dto.setName(u.getName());
		dto.setEmail(u.getEmail().toString());
		dto.setPicture(u.getPicture());
		dto.setPosts(baseUri.clone()
			.path("posts")
			.build().toASCIIString()
		);

		return dto;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getPosts() {
		return posts;
	}

	public void setPosts(String posts) {
		this.posts = posts;
	}
}
