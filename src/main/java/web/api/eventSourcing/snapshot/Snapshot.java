package web.api.eventSourcing.snapshot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import web.api.eventSourcing.event.model.CartRawEvent;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@RedisHash("Snapshot")
public class Snapshot<A> implements Serializable {

    private Long identifier;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long seq;

    private Long version;

    private A aggregateRoot;

    private LocalDateTime created;

    public Snapshot(Long identifier, Long version, A aggregateRoot) {
        this.identifier = identifier;
        this.version = version;
        this.aggregateRoot = aggregateRoot;
        this.created = LocalDateTime.now();
    }
}
