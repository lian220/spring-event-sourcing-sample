package web.api.eventSourcing.snapshot;

import lombok.Getter;
import web.api.domain.AggregateRoot;

import java.io.Serializable;

@Getter
public class Snapshot<A extends AggregateRoot, ID> implements Serializable {

    private ID identifier;

    private Long version;

    private A aggregateRoot;

    public Snapshot(ID identifier, Long version, A aggregateRoot) {
        this.identifier = identifier;
        this.version = version;
        this.aggregateRoot = aggregateRoot;
    }
}
