package web.api.eventSourcing.event.model;

public interface RawEvent<ID> {

    ID getIdentifier();

    String getType();

    Long getVersion();

    String getPayload();
}
