package web.api.eventSourcing.event;

import web.api.domain.AggregateRoot;

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

	/**
	 * Get the aggregate
	 *
	 * @param identifier
	 * @return
	 */
	A find(ID identifier) throws Exception;

	/**
	 * Get the All aggregate
	 * @return
	 */
	List<A> findAll() throws Exception;
}
