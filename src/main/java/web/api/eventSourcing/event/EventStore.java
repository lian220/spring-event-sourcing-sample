package web.api.eventSourcing.event;

import org.springframework.data.repository.Repository;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.model.Cart;

import java.util.List;

/**
 * Created by jaceshim on 2017. 3. 5..
 */
@org.springframework.stereotype.Repository
public interface EventStore<ID> extends Repository<Event, Long> {

	/**
	 * 이벤트를 저장한다
	 *
	 * @param cart
	 * @return
	 */
	CartRawEvent saveEvents(Cart cart);

	/**
	 * 주어진 identifier 의 저장된 이벤트를 얻는다
	 * @param identifier
	 * @return
	 */
	List<Event<ID>> getEvents(ID identifier);

	/**
	 * 저장된 모든 이벤트를 조회한다
	 * @return
	 */
	List<Event<ID>> getAllEvents();

	/**
	 * 주어진 identifier의 저장된 이벤트중 주어진 version이후 이벤트를 얻는다
	 * @param identifier
	 * @param version
	 * @return
	 */
	List<Event<ID>> getEventsByAfterVersion(ID identifier, Long version);
}
