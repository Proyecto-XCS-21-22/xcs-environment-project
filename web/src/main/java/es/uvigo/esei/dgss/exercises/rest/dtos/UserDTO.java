package es.uvigo.esei.dgss.exercises.rest.dtos;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.User;

public class UserDTO {
	private String login;
	private String name;
	private String email;
	private byte[] picture;
	private String postsResource;

	public static UserDTO of(User u, UriBuilder baseUri) {
		final UserDTO dto = new UserDTO();

		dto.setLogin(u.getLogin());
		dto.setName(u.getName());
		dto.setEmail(u.getEmail().toString());
		dto.setPicture(u.getPicture());
		dto.setPostsResource(baseUri.clone()
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

	public String getPostsResource() {
		return postsResource;
	}

	public void setPostsResource(String postsResource) {
		this.postsResource = postsResource;
	}
}
