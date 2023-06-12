package web.api.repository;

import org.springframework.data.repository.Repository;
import web.api.eventSourcing.event.Event;
import web.api.eventSourcing.event.model.CartRawEvent;

import java.util.List;

public interface EventStoreRepository  extends Repository<CartRawEvent, Long> {
    Event findBySeq(Long seq);

    CartRawEvent save(CartRawEvent event);

    List<CartRawEvent> findTop5ByIdentifierOrderBySeqDesc(Long identifier);
}
