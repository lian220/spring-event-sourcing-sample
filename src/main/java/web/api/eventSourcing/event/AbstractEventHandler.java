package web.api.eventSourcing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import web.api.domain.AggregateRoot;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.query.Cart;
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
public abstract class AbstractEventHandler<A extends AggregateRoot, ID> implements EventHandler<A,ID> {

	private final Class<A> aggregateType;

	private EventStoreRepository eventStoreRepository;

	private SnapshotRepository snapshotRepository;

	@Autowired
	private EventStore<ID> eventStore;

	private Map<Long, Integer> snapshotCountMap = new ConcurrentHashMap<Long, Integer>();
	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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

	private A createAggregateRootViaReflection(Long seq) {
		try {
			Constructor[] constructors = aggregateType.getDeclaredConstructors();
			for (Constructor constructor : constructors) {
				if (constructor.getParameterCount() == 1) {
					constructor.setAccessible(true);
					return (A) constructor.newInstance(seq);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		throw new IllegalArgumentException("Aggregate에 identifier를 argument로 받는 생성자가 없음");
	}

	@SneakyThrows
	@Override
	public void save(Cart cart) {
		// 이벤트 저장소에 이벤트 저장
		CartRawEvent rawEvent = eventStore.saveEvents(cart);

		//스냅샷 추가
		Integer snapshotCount = snapshotCountMap.get(cart.getMemberId());
		if (snapshotCount == null) {
			snapshotCount = 0;
		}

		if (snapshotCount == SNAPSHOT_COUNT) {
			log.debug("{} snapshot count {}", rawEvent.getSeq(), snapshotCount);
			List<CartRawEvent> events = eventStoreRepository.findTop5ByIdentifierOrderBySeqDesc(rawEvent.getIdentifier());
			AtomicInteger addEa = new AtomicInteger(0);
			events.stream().map(CartRawEvent::getPayload).forEach(obj -> {
				Map cartTemp = convertStringToMap(obj);
				addEa.addAndGet((Integer) cartTemp.get("ea"));
			});
			CartRawEvent snapshotCart = new CartRawEvent(cart.getMemberId(), "ADD CART", cart.getExpectedVersion(), objectMapper.writeValueAsString(addEa), LocalDateTime.now());
			Snapshot<A> snapshot = new Snapshot(rawEvent.getSeq(), rawEvent.getVersion(), snapshotCart);
			snapshotRepository.save(snapshot);
			snapshotCountMap.put(rawEvent.getIdentifier(), 0);

			return;
		}

		final int increaseCount = ++snapshotCount;
		snapshotCountMap.put(rawEvent.getIdentifier(), increaseCount);
		log.debug("{} snapshot increase count {}", increaseCount);
	}

	private Map convertStringToMap(String obj) {
		try {
			return objectMapper.readValue(obj, Map.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public A find(Long seq) throws Exception {
		// snapshot저장소에서 호출함

		A aggregateRoot = null;

		Snapshot retrieveSnapshot = retrieveSnapshot(seq);
		Cart cart;
		List<CartRawEvent> baseEvents;
		if (retrieveSnapshot!=null) {
			Snapshot<A> snapshot = retrieveSnapshot;
			baseEvents = eventStore.getEventsByAfterVersion(snapshot.getSeq(), snapshot.getVersion());
			// snapshot에 저장된 aggregateRoot객체를 로딩함.
			aggregateRoot = snapshot.getAggregateRoot();
		} else {
			baseEvents = eventStore.getEvents(seq);
		}

		if (baseEvents == null || baseEvents.size() == 0) {
			return aggregateRoot;
		}
//		aggregateRoot.replay(baseEvents);

		return aggregateRoot;
	}

	@Override
	public List<A> findAll() throws Exception {
		List<A> result = new ArrayList<>();

		return result;
	}

	/**
	 * Get the snapshot
	 * @param seq
	 * @return
	 */
	private Snapshot retrieveSnapshot(Long seq) {
		if (snapshotRepository == null) {
			return null;
		}
		List<Snapshot> lists = snapshotRepository.findAllByOrderByCreatedDesc();
		return lists.stream().filter((snapshot) -> snapshot.getSeq() == seq).findAny().get();
	}

}
