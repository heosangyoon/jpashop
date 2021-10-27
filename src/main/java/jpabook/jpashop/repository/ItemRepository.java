package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    // 컨트롤러에서 서비스의 savaItem 을 호출하고, 서비스에선 레포지토리의 save 를 호출해서 넘어온상태.
    // 인자로는 Item '엔티티'가 넘어왔음.
    // 여기서 아이디가있는 경우 호출될 merge 에 대한 설명.... 필요없고 그냥 쓰지 마세요
    // 무조건 변경감지 하세요 ^_^
    public void save(Item item){
        if(item.getId() == null) em.persist(item);
        else em.merge(item); // 지금은 일단 아이템을 업데이트 하는거랑 비슷하다고 생각하자
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
