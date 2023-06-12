package web.api.repository;

import org.springframework.data.repository.CrudRepository;
import web.api.eventSourcing.snapshot.Snapshot;

import java.util.List;
import java.util.Optional;
@org.springframework.stereotype.Repository
public interface SnapshotRepository extends CrudRepository<Snapshot, Long> {

    Optional<Snapshot> findFirstOrderBySeq(Long seq);

    Snapshot save(Snapshot snapshot);

    List<Snapshot> findAllByOrderByCreatedDesc();
}
