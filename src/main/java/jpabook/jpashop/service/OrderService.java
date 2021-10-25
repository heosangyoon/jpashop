package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 그냥 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보를 여기서 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // 회원의 주소정보를 그냥 가져왔는데, 다른 사람한테 선물해주는거일수도 있잖수
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // 근데 이렇게 스태틱 생성자를 사용해서 만들어주는데, 나중에 그냥 기본 생성자 new OrderItem(); 써서 만들려고 할 수도 있음.
        // 물론 그렇게 되면 이 메소드 저 메소드 로직이 달라지게 되고, 그럼 유지보수에 상당한 애로사항이 생김
        // 이걸 막을 방법은, 아무 로직도 없는 proected 생성자를 만드는 것임.
        // 그렇게 처리해놓으면 new OrderItem() 부분에 빨간줄이 들어오게 됨. 아 이거 쓰지말고 스태틱 생성자 쓰란 말이구나 하고 알아먹음 됨.
        // 근데 이것도 @NoArgsConstructor(access = AccessLevel.PROTECTED) 어노테이션을 클래스에 붙이면 같은 동작을 함. 갓롬복 ...

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    //검색
     
}
