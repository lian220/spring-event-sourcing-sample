package web.api.eventSourcing.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.api.eventSourcing.event.model.CartRawEvent;
import web.api.eventSourcing.model.Cart;
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
        String payload = getStringJson(cart);
        CartRawEvent rawEvent = new CartRawEvent(cart.getMemberId(), "ADD CART", cart.getExpectedVersion(), payload, LocalDateTime.now());
        CartRawEvent lastVersionEvent = eventStoreRepository.findFirstByIdentifierOrderByVersionDesc(cart.getMemberId());
        long lastVersion = lastVersionEvent != null ? lastVersionEvent.getVersion() : 0;
        long actualVersion = cart.getVersion();
        if (cart.getVersion() != lastVersion) {
            String exceptionMessage = String.format("Unmatched Version : expected: {}, actual: {}", lastVersion, actualVersion);
            throw new IllegalStateException(exceptionMessage);
        }
        rawEvent.setVersion(cart.getVersion() + 1);
        eventStoreRepository.save(rawEvent);

        eventProjector.handle(cart);

        return rawEvent;
    }

    @Override
    public List<Event<Long>> getEvents(Long identifier) {
        return null;
    }

    @Override
    public List<Event<Long>> getAllEvents() {
        return null;
    }

    @Override
    public List<Event<Long>> getEventsByAfterVersion(Long identifier, Long version) {
        return null;
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
