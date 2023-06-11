package web.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import web.api.eventSourcing.event.Event;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaceshim on 2017. 3. 5..
 */
@Slf4j
@JsonIgnoreProperties({ "identifier", "expectedVersion", "uncommittedChanges" })
public abstract class AggregateRoot<ID> implements Serializable {

	private ID identifier;

	private Long expectedVersion = 0L;

	private List<Event> changeEvents = new ArrayList<>();

	public AggregateRoot(ID identifier) {
		this.identifier = identifier;
	}

	public AggregateRoot() {

	}

	public void markChangesAsCommitted() {
		this.changeEvents.clear();
	}

	public List<Event> getUncommittedChanges() {
		return this.changeEvents;
	}

	public ID getIdentifier() {
		return identifier;
	}

	public Long getExpectedVersion() {
		return expectedVersion;
	}

	public void replay(List<Event> changes) throws Exception {
		for (Event event : changes) {
			applyChange(event, false);
			this.expectedVersion++;
		}
	}

	protected void applyChange(Event change) throws Exception {
		applyChange(change, true);
	}

	private static final String APPLY_METHOD_NAME = "apply";
	private void applyChange(Event event, boolean isNew) throws Exception {
		Method method;
		try {
			method = this.getClass().getDeclaredMethod(APPLY_METHOD_NAME, event.getClass());
			if (method != null) {
				method.setAccessible(true);
				method.invoke(this, event);
			}

			if (isNew) {
				changeEvents.add(event);
			}
		} catch (IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage(), e);
			throw new Exception(e.getMessage(), e);
		}
	}
}
