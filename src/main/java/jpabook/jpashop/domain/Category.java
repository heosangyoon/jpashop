package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // ManyToMany 현업에서는 "절대" 안쓴다는데요! 관계형 DB 에서는 정규화 테이블 2개로 다대다를 표현 불가.
    // 조인테이블을 추가해서 일대다 - 다대일 이렇게 연결해줘야한다.
    // 이 프로젝트에서는 Category 와 Item 사이에 category_item 이라는 넘이 조인테이블 역할이다!
    // JPA 에서 ManyToMany 를 사용하면 기본키 외래키 쌍을 알아서 매핑까진 해주지만, 그 외의 정보는 조인테이블에 담을 수 없다.
    // 그러나 실무에서는 비즈니스에 필요한 여러 정보들(날짜, 시간 등등)이 담겨야 할 경우도 많기 때문에
    // ManyToMany 보다는 조인테이블을 직접 만들고, OneToMany - 조인 테이블 - ManyToOne 형식으로 관계를 매핑해야한다.
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    // 33~38 라인은 카테고리끼리 매핑을 시켜줌.(부모-자식 인데 같은 종류끼리. 카테고리는 트리처럼 생겼으니까 (?))
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
