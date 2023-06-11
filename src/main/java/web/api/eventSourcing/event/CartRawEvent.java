package web.api.eventSourcing.event;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "raw_event")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "seq" })
@NoArgsConstructor
public class CartRawEvent implements RawEvent<Long>{
    /** seq */
    @Id
    @Column(name = "seq", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long seq;

    /**  이벤트 식별자 */
    @Column(name = "identifier", nullable = false)
    private Long identifier;

    /** 이벤트 유형 */
    @Column(name = "type", nullable = false)
    private String type;

    /** 이벤트 버전 */
    @Column(name="version", nullable = false)
    private Long version;

    /** 이벤트 payload */
    @Column(name="payload", nullable = false)
    private String payload;

    /** 이벤트 생성일시 */
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public CartRawEvent(Long identifier, String type, Long version, String payload, LocalDateTime created) {
        this.identifier = identifier;
        this.type = type;
        this.version = version;
        this.payload = payload;
        this.created = created;
    }
}
