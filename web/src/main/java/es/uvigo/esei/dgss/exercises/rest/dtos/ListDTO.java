package es.uvigo.esei.dgss.exercises.rest.dtos;

import java.util.List;

public class ListDTO<T> {
	private List<T> objects;

	protected ListDTO() {}

	public ListDTO(List<T> objects) {
		this.objects = objects;
	}

	public List<T> getObjects() {
		return objects;
	}

	public void setObjects(List<T> objects) {
		this.objects = objects;
	}
}
