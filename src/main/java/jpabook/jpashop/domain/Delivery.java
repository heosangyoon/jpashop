package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    // Delivery 와 Order 는 1:1 관계지만 Order 에 외래키가 가까우므로 주인. 주인이 아닌넘한텐 이렇게 mappedBy 가 붙는다.
    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum 쓸 때 조심할게 디폴트가 ORDINAL 인데, 이게 1 2 3 4 숫자형임. 무조건 STRING 으로 해야함.
    private DeliveryStatus status;
}
