package web.api.eventSourcing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.query.Cart;
import web.api.repository.EventStoreRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CartEventStore implements EventStore<Long> {

    @Autowired
    private EventProjector eventProjector;

    @Autowired
    private EventStoreRepository eventStoreRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Override
    public CartRawEvent saveEvents(Cart cart) {
        //1. 마지막 버전과 일치 확인
        String type = "ADD CART";
        CartRawEvent lastVersionEvent = eventStoreRepository.findFirstByIdentifierOrderByVersionDesc(cart.getMemberId());
        if(lastVersionEvent == null) {
            type = "CREATE CART";
        }
        long lastVersion = lastVersionEvent != null ? lastVersionEvent.getVersion() : 0;
        long actualVersion = cart.getVersion();
        if (cart.getVersion() != lastVersion) {
            String exceptionMessage = String.format("Unmatched Version : expected: {}, actual: {}", lastVersion, actualVersion);
            throw new IllegalStateException(exceptionMessage);
        }

        //2. 이벤트 스토어 저장 객체 생성 및 저장
        String payload = getStringJson(cart);
        CartRawEvent rawEvent = new CartRawEvent(cart.getMemberId(), type, cart.getExpectedVersion(), payload, LocalDateTime.now());
        rawEvent.setVersion(cart.getVersion() + 1);
        try{
            eventStoreRepository.save(rawEvent);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        //3. query Model 동기화
        eventProjector.handle(cart);

        return rawEvent;
    }

    @Override
    public List<CartRawEvent> getEvents(Long seq) {
        final List<CartRawEvent> rawEvents = eventStoreRepository.findAllBySeq(seq);
        return rawEvents;
    }

    @Override
    public List<Event<Long>> getAllEvents() {
        return null;
    }

    @Override
    public List<CartRawEvent> getEventsByAfterVersion(Long identifier, Long seq) {
        final List<CartRawEvent> rawEvents = eventStoreRepository.findAllBySeqGreaterThan(seq);
        return rawEvents;
    }

    private String getStringJson(Cart cart) {
        try {
            return objectMapper.writeValueAsString(cart);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return "";
    }
}
