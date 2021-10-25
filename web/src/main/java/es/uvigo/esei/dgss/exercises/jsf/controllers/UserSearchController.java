package es.uvigo.esei.dgss.exercises.jsf.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.service.StatisticsEJB;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Named("userSearchController")
@SessionScoped
public class UserSearchController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserEJB users;

	@Inject
	private StatisticsEJB stats;

	private String searchQuery;

	private Collection<User> searchResults;

	private User selectedUser;

	public long getPostCount() {
		return stats.getPostCount();
	}

	public long getUserCount() {
		return stats.getUserCount();
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public Collection<User> getSearchResults() {
		return searchResults;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public void doSearch() {
		this.searchResults = users.search(searchQuery);
	}

	public void doUserSelection(SelectEvent<User> event) throws IOException {
		selectedUser = null;

		FacesContext.getCurrentInstance().getExternalContext()
			.redirect("user.xhtml?login=" + event.getObject().getLogin());
	}
}
