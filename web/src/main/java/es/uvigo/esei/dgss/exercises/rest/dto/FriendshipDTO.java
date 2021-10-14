package es.uvigo.esei.dgss.exercises.rest.dto;

import static es.uvigo.esei.dgss.exercises.rest.dto.Constants.DATE_FORMATTER;
import static es.uvigo.esei.dgss.exercises.rest.dto.Constants.USER_RESOURCE;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.Friendship;

public class FriendshipDTO {
	private String sender;
	private String receiver;
	private String date;
	private boolean accepted;

	public static FriendshipDTO of(Friendship f, UriBuilder baseUri) {
		final FriendshipDTO dto = new FriendshipDTO();

		dto.setSender(USER_RESOURCE.apply(f.getSender(), baseUri));
		dto.setReceiver(USER_RESOURCE.apply(f.getReceiver(), baseUri));
		dto.setDate(DATE_FORMATTER.apply(f.getDate()));
		dto.setAccepted(f.isAccepted());

		return dto;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
