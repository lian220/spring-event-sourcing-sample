package web.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.api.domain.Member;
import web.api.repository.MemberJpaRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    /**
     * 회원 전체 조회
     */
    public List<Member> findAllMember() {
        return memberJpaRepository.findAll();
    }

    /**
     * 회원 조회
     */
    public Member findMember(Long memberId) {
        return memberJpaRepository.findByMemberId(memberId);
    }

    /**
     * 회원 등록
     */
    @Transactional
    public Member saveMember(Member member) {
        return memberJpaRepository.save(member);
    }

    /**
     * 회원 수정
     */
    @Transactional
    public Member editMember(Long memberId, Member member) {
        Member findMember = memberJpaRepository.findByMemberId(memberId);
        if (member.getName() != null) {
            findMember.setName(member.getName());
        }
        if (member.getBirth() != null) {
            findMember.setBirth(member.getBirth());
        }

        // return memberJpaRepository.save(findMember); // Dirty Checking
        return findMember;
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        memberJpaRepository.deleteById(memberId);
    }

}
