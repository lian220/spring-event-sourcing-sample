package web.api.eventSourcing.snapshot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import web.api.domain.AggregateRoot;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@RedisHash("Snapshot")
public class Snapshot<A extends AggregateRoot> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    private Long seq;

    private Long version;

    private A aggregateRoot;

    private LocalDateTime created;

    public Snapshot(Long seq, Long version, A aggregateRoot) {
        this.seq = seq;
        this.version = version;
        this.aggregateRoot = aggregateRoot;
        this.created = LocalDateTime.now();
    }
}
