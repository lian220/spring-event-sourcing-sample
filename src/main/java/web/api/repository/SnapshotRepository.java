package web.api.repository;

import web.api.domain.AggregateRoot;
import web.api.eventSourcing.snapshot.Snapshot;

import java.util.Optional;
public interface SnapshotRepository<A extends AggregateRoot, ID> {

    Optional<Snapshot<A, ID>> findLatest(ID id);

    void save(Snapshot<A, ID> snapshot);
}
