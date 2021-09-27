package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String login;

	@Column(nullable = false, length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String name;

	@Column(nullable = false, length = 64)
	@NotNull @Size(min = 8, max = 64)
	private String password;

	@Size(max = 2 * 1024 * 1024)
	private byte[] picture;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author", orphanRemoval = true)
	private Set<Post> posts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author", orphanRemoval = true)
	private Set<Comment> comments;

	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Post> likedPosts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "sender", orphanRemoval = true)
	private Set<Friendship> sentFriendships;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver", orphanRemoval = true)
	private Set<Friendship> receivedFriendships;

	protected User() {}

	public User(String login, String password) {
		this.login = Objects.requireNonNull(login);
		this.password = Objects.requireNonNull(password);
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = Objects.requireNonNull(password);
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public Collection<Post> getPosts() {
		return posts;
	}

	public Collection<Comment> getComments() {
		return comments;
	}

	public Collection<Friendship> getSentFriendships() {
		return sentFriendships;
	}

	public Collection<Friendship> getReceivedFriendships() {
		return receivedFriendships;
	}

	public Collection<User> getFriends() {
		return Stream.concat(
			getSentFriendships().stream().map((Friendship f) -> f.getReceiver()),
			getReceivedFriendships().stream().map((Friendship f) -> f.getSender())
		).collect(Collectors.toSet());
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = obj instanceof User;

		if (equal && this != obj) {
			User other = (User) obj;
			equal = Objects.equals(login, other.login);
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}
}
