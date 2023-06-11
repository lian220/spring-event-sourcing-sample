package web.api.eventSourcing.event;

public abstract class AbstractCartEvent implements Event<Long>{
    protected Long cartId;
    @Override
    public Long getIdentifier() {
        return this.cartId;
    }
}
