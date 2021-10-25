package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 부모클래스에서 상속 전략을 잡아줘야함! SINGLE_TABLE 은 한 테이블에 때려박기
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    // 데이터를 가지고 있는 부분에 비즈니스 로직을 구현하는 것이 가장 좋다! 그래야 응집력이 높다!
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("씨가 말라부렀슈");
        }
        this.stockQuantity = restStock;
    }
}

// addStock 과 removeStock 합쳐놓고 상품 팔릴땐 인자를 음수로 줄 수 없나 ...
// 주문쪽 비즈니스 로직을 살펴본다음에 결정할 수 있을듯하다.