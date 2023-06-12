package web.api.eventSourcing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import web.api.domain.AggregateRoot;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.model.Cart;
import web.api.eventSourcing.snapshot.Snapshot;
import web.api.repository.EventStoreRepository;
import web.api.repository.SnapshotRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jaceshim on 2017. 3. 5..
 */
@Slf4j
public abstract class AbstractEventHandler<A extends AggregateRoot, ID> implements EventHandler<A, ID> {

	private final Class<A> aggregateType;

	private EventStoreRepository eventStoreRepository;

	private SnapshotRepository snapshotRepository;

	private Map<Long, Integer> snapshotCountMap = new ConcurrentHashMap<Long, Integer>();

	private static final int SNAPSHOT_COUNT = 2;

	public AbstractEventHandler(EventStoreRepository eventStore, SnapshotRepository snapshotRepository) {
		this.eventStoreRepository = eventStore;
		this.snapshotRepository = snapshotRepository;
		this.aggregateType = aggregateType();
	}

	private Class<A> aggregateType() {
		Type genType = this.getClass().getGenericSuperclass();
		if (genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

			if ((params != null) && (params.length >= 1)) {
				return (Class) params[0];
			}
		}
		return null;
	}

	private A createAggregateRootViaReflection(ID identifier) {
		try {
			Constructor[] constructors = aggregateType.getDeclaredConstructors();
			for (Constructor constructor : constructors) {
				if (constructor.getParameterCount() == 1) {
					constructor.setAccessible(true);
					return (A) constructor.newInstance(identifier);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		throw new IllegalArgumentException("Aggregate에 identifier를 argument로 받는 생성자가 없음");
	}

	@Override
	public void save(Cart cart) {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		// 이벤트 저장소에 이벤트 저장
		String payload = "";
		try {
			payload = objectMapper.writeValueAsString(cart);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		CartRawEvent rawEvent = new CartRawEvent(cart.getMemberId(), "ADD CART", cart.getExpectedVersion(), payload, LocalDateTime.now());
		eventStoreRepository.save(rawEvent);


		// 미처리된 이벤트 목록 clear
		Integer snapshotCount = snapshotCountMap.get(rawEvent.getIdentifier());
		if (snapshotCount == null) {
			snapshotCount = 0;
		}

		if (snapshotCount == SNAPSHOT_COUNT) {
			log.debug("{} snapshot count {}", rawEvent.getSeq(), snapshotCount);
			List<CartRawEvent> events = eventStoreRepository.findTop5ByIdentifierOrderBySeqDesc(rawEvent.getIdentifier());
			AtomicInteger addEa = new AtomicInteger(0);
			events.stream().map(CartRawEvent::getPayload).forEach(obj -> {
				Map cart1 = null;
				try {
					cart1 = objectMapper.readValue(obj, Map.class);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				addEa.addAndGet((Integer) cart1.get("ea"));
			});
			CartRawEvent snapshotCart = null;
			try {
				snapshotCart = new CartRawEvent(cart.getMemberId(), "ADD CART", cart.getExpectedVersion(), objectMapper.writeValueAsString(addEa), LocalDateTime.now());
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			Snapshot snapshot = new Snapshot(rawEvent.getSeq(), rawEvent.getVersion(), snapshotCart);
			snapshotRepository.save(snapshot);
			snapshotCountMap.put(rawEvent.getIdentifier(), 0);

			return;
		}

		final int increaseCount = ++snapshotCount;
		snapshotCountMap.put(rawEvent.getIdentifier(), increaseCount);
		log.debug("{} snapshot increase count {}", increaseCount);
	}

	@Override
	public A find(ID identifier) throws Exception {
		// snapshot저장소에서 호출함
		A aggregateRoot = createAggregateRootViaReflection(identifier);

//		Optional<Snapshot<A, ID>> retrieveSnapshot = retrieveSnapshot(identifier);
//		List<Event<ID>> baseEvents;
//		if (retrieveSnapshot.isPresent()) {
//			Snapshot<A, ID> snapshot = retrieveSnapshot.get();
//			baseEvents = eventStore.getEventsByAfterVersion(snapshot.getIdentifier(), snapshot.getVersion());
//			// snapshot에 저장된 aggregateRoot객체를 로딩함.
//			aggregateRoot = snapshot.getAggregateRoot();
//		} else {
//			baseEvents = eventStore.getEvents(identifier);
//		}
//
//		if (baseEvents == null || baseEvents.size() == 0) {
//			return null;
//		}
//
//		aggregateRoot.replay(baseEvents);

		return aggregateRoot;
	}

	@Override
	public List<A> findAll() throws Exception {
		List<A> result = new ArrayList<>();

//		final List<Event<ID>> allEventsOpt = eventStore.getAllEvents();
//		if (allEventsOpt == null) {
//			return result;
//		}
//
//		final Map<ID, List<Event<ID>>> eventsByIdentifier = allEventsOpt.stream().collect(groupingBy(Event::getIdentifier));
//		for (Map.Entry<ID, List<Event<ID>>> entry : eventsByIdentifier.entrySet()) {
//			A aggregateRoot = createAggregateRootViaReflection(entry.getKey());
//			aggregateRoot.replay(entry.getValue());
//
//			result.add(aggregateRoot);
//		}

		return result;
	}

	/**
	 * Get the snapshot
	 * @param identifier
	 * @return
	 */
	private Optional<Snapshot> retrieveSnapshot(Long seq) {
		if (snapshotRepository == null) {
			return Optional.empty();
		}
		return snapshotRepository.findFirstOrderBySeq(seq);
	}

}
