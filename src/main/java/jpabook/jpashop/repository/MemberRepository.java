package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 이렇게 해놓으면 스프링이 엔티티 매니저를 만들어서 여기 주입해준다? @RequiredArgsConstructor 와 세트임
    private final EntityManager em;

    public void save(Member member){
        em.persist(member); // persist 는 객체를 보관(?) 근데 DB 에 insert 를 하는 것은 아니다!
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); // 하나만 찾음
    }

    // JPQL 을 이용. 모든 건수를 조회
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    // 요것 역시 JPQL 을 이용, id 가 아니라 이름으로 조회
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name= :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
