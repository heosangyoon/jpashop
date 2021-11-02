package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional // 요 어노테이션(javax 말고 spring)을 클래스에 걸어주게 되면 안에있는 public 메서드들에 트랜잭션이 걸리게된다
@Transactional(readOnly = true) // 이렇게 걸면 모든 public 메서드에 자동으로 읽기 전용 어노테이션이 걸리게 되고,
public class MemberService {

    // 요즘은 클래스에 @RequiredArgsConstructor 를 달고 이렇게 주입하는게 좋다고한다.
    private final MemberRepository memberRepository;


    //회원 가입
    @Transactional // 데이터를 입력, 수정하는 메서드에만 이렇게 붙이면 더 간결해지게찌
    public Long join(Member member){
        validateDuplicatedMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicatedMember(Member member){ //이름으로 검색
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("존재하는 회원입니다람쥐");
        }
    }

    //회원 전체 조회
    //@Transactional(readOnly = true) // 요렇게 readOnly 를 걸어주면 JPA 가 조회할 때 성능이 최적화된다.
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //@Transactional(readOnly = true)
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
