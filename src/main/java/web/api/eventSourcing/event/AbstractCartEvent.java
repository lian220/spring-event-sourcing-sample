package web.api.eventSourcing.event;

public abstract class AbstractCartEvent implements Event<Long>{
    protected Long memberId;
    @Override
    public Long getIdentifier() {
        return this.memberId;
    }
}
