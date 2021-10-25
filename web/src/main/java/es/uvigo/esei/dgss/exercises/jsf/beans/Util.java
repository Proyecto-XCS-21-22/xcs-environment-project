package es.uvigo.esei.dgss.exercises.jsf.beans;

import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringJoiner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("util")
@ApplicationScoped
public class Util {
	public String pictureToGraphicURL(byte[] picture) {
		final StringBuilder sb = new StringBuilder();

		sb.append("data:image/*;base64,");
		sb.append(Base64.getEncoder().encodeToString(picture));

		return sb.toString();
	}

	public <T extends CharSequence> String commaJoinSome(Collection<? extends CharSequence> elements, T emptyValue) {
		final StringJoiner joiner = new StringJoiner(", ");
		final Iterator<? extends CharSequence> iter = elements.iterator();
		final int elementCount = elements.size();
		int i = 0;

		joiner.setEmptyValue(emptyValue);

		while (i < elementCount && i++ < 15) {
			joiner.add(iter.next());
		}

		// If we have omitted some elements, signal it
		if (i < elementCount - 1) {
			joiner.add("...");
		}

		return joiner.toString();
	}
}
