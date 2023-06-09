package web.api.eventSourcing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import web.api.domain.AggregateRoot;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.query.Cart;

import java.util.List;

/**
 * Created by jaceshim on 2017. 3. 3..
 */
public interface EventHandler<A extends AggregateRoot, ID> {

	/**
	 * Save the aggregate
	 *
	 * @param aggregate
	 */
	void save(A aggregate);

	void save(Cart cart) throws JsonProcessingException;

	/**
	 * Get the aggregate
	 *
	 * @param identifier
	 * @return
	 */
	A find(ID identifier) throws Exception;
	A find(Long seq) throws Exception;

//	CartRawEvent find(Long seq) throws Exception;

	/**
	 * Get the All aggregate
	 * @return
	 */
	List<A> findAll() throws Exception;
}
