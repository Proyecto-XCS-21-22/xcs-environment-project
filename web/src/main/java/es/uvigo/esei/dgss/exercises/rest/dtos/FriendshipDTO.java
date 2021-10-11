package es.uvigo.esei.dgss.exercises.rest.dtos;

import static es.uvigo.esei.dgss.exercises.rest.dtos.Constants.DATE_FORMATTER;
import static es.uvigo.esei.dgss.exercises.rest.dtos.Constants.USER_RESOURCE;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.Friendship;

public class FriendshipDTO {
	private String senderResource;
	private String receiverResource;
	private String date;
	private boolean accepted;

	public static FriendshipDTO of(Friendship f, UriBuilder baseUri) {
		final FriendshipDTO dto = new FriendshipDTO();

		dto.setSenderResource(USER_RESOURCE.apply(f.getSender(), baseUri));
		dto.setReceiverResource(USER_RESOURCE.apply(f.getReceiver(), baseUri));
		dto.setDate(DATE_FORMATTER.apply(f.getDate()));
		dto.setAccepted(f.isAccepted());

		return dto;
	}

	public String getSenderResource() {
		return senderResource;
	}

	public void setSenderResource(String senderResource) {
		this.senderResource = senderResource;
	}

	public String getReceiverResource() {
		return receiverResource;
	}

	public void setReceiverResource(String receiverResource) {
		this.receiverResource = receiverResource;
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
