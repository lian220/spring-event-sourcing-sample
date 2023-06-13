package web.api.eventSourcing.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import web.api.eventSourcing.query.Cart;
import web.api.repository.EventStoreRepository;
import web.api.repository.SnapshotRepository;

@Component
public class CartEventHandler extends AbstractEventHandler<Cart, Long> {

    @Autowired
    public CartEventHandler(EventStoreRepository eventStoreRepository,
                            SnapshotRepository snapshotRepository) {
        super(eventStoreRepository, snapshotRepository);
    }
}
