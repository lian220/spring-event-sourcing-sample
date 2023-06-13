package web.api.eventSourcing.event;

import web.api.eventSourcing.query.Cart;

public interface EventProjector {
    void handle(Cart event);

    void handle(Event event);
}
