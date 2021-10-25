package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // ManyToOne 과 OneToMany 는 짝을 이뤄야한다. 이 프로젝트에서는 Member - Order
    // 그리고 얘가 Member - Order 관계에서 주인임. (외래키가 가까운 쪽)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    // 이번엔 Order 가 OrderItem 과의 관계에서 주인이 아님.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // JPA 에서 cascade 는 persist 를 자동으로 해주는 역할을 한다.
    private List<OrderItem> orderItems = new ArrayList<>();


    // 그리고 fetch 의 타입을 무조건 LAZY (지연로딩) 으로 설정해주어야 한다.
    // EAGER (즉시로딩)으로 설정하게 되면, 어떤 SQL 이 수행될지 예측할 수 없고,
    // 최악의 경우에는 연관된 모든 데이터를 끌어오려고 한다. 그래서 EAGER 로 설정하면 절대!!!!! 안된다.
    // 이 프로젝트 같은 경우는 Member 만 조회했는데 연관된 Order, OrderItem, Item, Category 등 전부다 가져와버리면
    // 성능에 심각한 결함이 생김. 난리가 난거야.. 실무에서도 EAGER 때문에 문제 생기는 경우가 많다고 한다.
    // 일단 모든 ManyToOne OneToOne 관계를 LAZY 로 설정해놓고, 나주엥 필요할때 fetch join 으로 최적화를 해주는것이 정석!
    // LAZY exception 발생한다고해도 다른 해결방법이 있으니까 EAGER 는 저어어어어어어얼대 쓰지 마세여
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태


    // 양방향 연관관계를 위한 메서드. 컨트롤 하는 쪽이 이 메서드를 들고 있는 것이 좋다.
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 별도의 생성 메서드... 여러 엔티티와 얽힌 복잡한 객체를 생성하는 메서드는 public static 으로 따로 만들어주면 조타.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    // 비지니스 로지꾸
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("방금 출발했어요");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }

    public int getTotalPrice(){
        int total = 0;
        for(OrderItem orderItem: orderItems){
            total += orderItem.getTotalPrice();
        }

        return total;
    }
}
