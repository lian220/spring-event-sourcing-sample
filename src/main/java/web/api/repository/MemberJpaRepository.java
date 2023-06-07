package web.api.repository;

import org.springframework.data.repository.Repository;
import web.api.domain.Member;

import java.util.List;

public interface MemberJpaRepository extends Repository<Member, Long> {

    Member findByMemberId(Long memberId);

    List<Member> findAll();

    Member save(Member member);

    void deleteById(Long memberId);
}
