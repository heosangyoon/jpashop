package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


// 보통 Getter Setter 안쓰고 직접 메소드 만드는게 가장 "이상적이다"
// 근데 실무에선 데이터 조회할 일이 너무 많으니까 Getter 는 쓰기 마련인데,
// Setter 는 데이터를 바꾸는 넘이니까. 이걸 너무 남발하면 데이터가 어떻게 언제 바뀌는지 파악하기 어려워짐.
// 때문에 변경 지점이 명확하도록 비즈니스 메서드를 별도로 제공하는 것이 정석이다!
@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id") // DB 업무를 하다보면 이게 어느 테이블의 'id' 인지 알아보기 어려울때가 있기때문에 이렇게 정해준다.
    private Long id;

    private String name;

    @Embedded // @Embeddable 필요
    private Address address;

    @OneToMany(mappedBy = "member") // 나는 연관관계의 주인이 아니예용 ! 읽기전용! 여기 무슨 값을 넣어도 안바뀐다!
    private List<Order> orders = new ArrayList<>();
}
