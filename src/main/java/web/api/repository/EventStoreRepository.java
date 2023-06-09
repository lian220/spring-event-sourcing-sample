package web.api.repository;

import org.springframework.data.repository.Repository;
import web.api.eventSourcing.event.Event;
import web.api.eventSourcing.event.model.CartRawEvent;

import java.util.List;

public interface EventStoreRepository  extends Repository<CartRawEvent, Long> {
    Event findBySeq(Long seq);

    CartRawEvent save(CartRawEvent event);

    CartRawEvent findFirstByIdentifierOrderByVersionDesc(Long identifier);

    List<CartRawEvent> findTop5ByIdentifierOrderBySeqDesc(Long identifier);

//    List<CartRawEvent> findBySeqAndVersionGreaterThan(Long seq, Long version);

    List<CartRawEvent> findAllBySeq(Long seq);

//    List<CartRawEvent> findBySeqAndIdentifierGreaterThan(Long seq, Long identifier);

    List<CartRawEvent> findAllBySeqAndIdentifierGreaterThan(Long seq, Long identifier);

    List<CartRawEvent> findAllBySeqGreaterThan(Long seq);
}
