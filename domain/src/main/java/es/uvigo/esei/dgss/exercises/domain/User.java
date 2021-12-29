package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import es.uvigo.esei.dgss.exercises.domain.constraints.ValidAddress;

@Entity
public class User implements Serializable {
	static final DigestUtils PASSWORD_HASHER = new DigestUtils(MessageDigestAlgorithms.MD5);

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String login;

	@Column(nullable = false, length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String name;

	@Column(nullable = false, length = 32)
	@NotNull
	private String password;

	@Column(nullable = false)
	@NotNull @ValidAddress
	private InternetAddress email;

	@Size(max = 2 * 1024 * 1024)
	private byte[] picture;

	@Column(nullable = false)
	@NotNull
	private String role = "user";

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author", orphanRemoval = true)
	private Set<Post> posts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author", orphanRemoval = true)
	private Set<Comment> comments;

	@ManyToMany(cascade = CascadeType.MERGE)
	private Set<Post> likedPosts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "sender", orphanRemoval = true)
	private Set<Friendship> sentFriendships;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver", orphanRemoval = true)
	private Set<Friendship> receivedFriendships;

	protected User() {}

	public User(String login, String name, InternetAddress email, String password) {
		this.login = Objects.requireNonNull(login);
		this.name = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
		setPassword(password);
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		password = Objects.requireNonNull(password);

		if (password.length() < 8) {
			throw new IllegalArgumentException("The password is too short");
		}

		this.password = PASSWORD_HASHER.digestAsHex(password);
	}

	public InternetAddress getEmail() {
		return email;
	}

	public void setEmail(InternetAddress email) {
		this.email = Objects.requireNonNull(email);
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getRole() {
		return role;
	}

	public Collection<Post> getPosts() {
		return Collections.unmodifiableSet(posts);
	}

	public Collection<Post> getLikedPosts() {
		return Collections.unmodifiableSet(likedPosts);
	}

	public Collection<Comment> getComments() {
		return Collections.unmodifiableSet(comments);
	}

	public Collection<Friendship> getSentFriendships() {
		return Collections.unmodifiableSet(sentFriendships);
	}

	public Collection<Friendship> getReceivedFriendships() {
		return Collections.unmodifiableSet(receivedFriendships);
	}

	public boolean likePost(Post post) {
		return likedPosts.add(post);
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
