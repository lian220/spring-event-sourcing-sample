package web.api.eventSourcing.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by jaceshim on 2017. 3. 7..
 */
public interface Event<Long> extends Serializable {
	@JsonIgnore
	Long getIdentifier();
}
