package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.rtner.security.auth.spi.SimplePBKDF2;

@Entity
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	// Use PBKDF2 as a password hashing algorithm because it's much safer than MD5,
	// as it provides key stretching by using salts, and it has a configurable number
	// of iterations that allows increasing the hashing cost as computers get more
	// powerful. RFC 8018 recommends PBKDF2 for password hashing.
	//
	// I also was curious about how to properly deal with passwords in a Java EE
	// application. (Side note: a char[] is better when it comes to storing passwords
	// in memory, because it can be cleared out more easily when needed. But working
	// with char[] is more cumbersome, and if an attacker has access to the main
	// memory things are screwed anyway. See: https://stackoverflow.com/a/8881376/9366153)
	//
	// See the jboss-web.xml file for more details
	private static final Function<String, String> PASSWORD_HASHER = (String password) ->
		new SimplePBKDF2(8, 25000).deriveKeyFormatted(password);

	@Id
	@Column(length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String login;

	@Column(nullable = false, length = 64)
	@NotNull @Size(min = 1, max = 64)
	private String name;

	@Column(nullable = false, length = 64)
	@NotNull
	private String password;

	@Column(nullable = false)
	@NotNull
	private InternetAddress email;

	@Size(max = 2 * 1024 * 1024)
	private byte[] picture;

	@Column(nullable = false)
	@NotNull
	private String role = "user";

	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "author")
	private Set<Post> posts;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "author")
	private Set<Comment> comments;

	@ManyToMany(cascade = CascadeType.MERGE)
	private Set<Post> likedPosts;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "sender", orphanRemoval = true)
	private Set<Friendship> sentFriendships;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "receiver", orphanRemoval = true)
	private Set<Friendship> receivedFriendships;

	protected User() {}

	public User(String login, String name, InternetAddress email, String password) {
		try {
			email.validate();
		} catch (final AddressException exc) {
			throw new IllegalArgumentException(exc);
		}

		this.login = Objects.requireNonNull(login);
		this.name = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
		this.password = PASSWORD_HASHER.apply(Objects.requireNonNull(password));
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

	public void setPassword(String password) {
		this.password = PASSWORD_HASHER.apply(Objects.requireNonNull(password));
	}

	public InternetAddress getEmail() {
		return email;
	}

	public void setEmail(InternetAddress email) {
		try {
			email.validate();
		} catch (final AddressException exc) {
			throw new IllegalArgumentException(exc);
		}

		this.email = email;
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

	public void likePost(Post post) {
		likedPosts.add(post);
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
