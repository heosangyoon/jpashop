package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/*
* Order 조회
* Order -> Member 연관 N:1
* Order -> Delivery 연관 1:1
* */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // 이렇게하면 무한루프 걸려요. 왜요?
    // all 이 일단은 Order 다. 여기 가보면, JSON 이 Member 를 뿌리게 된다는걸 알 수 있음.
    // 근데 Member 를 가보면 여기에 또 Orders 가 있음.
    // 그래서 Order 를 또 가 !
    // JSON 안에서 JSON 만들고 , 그 안에서 또 JSON 만들고 이 짓거리가 반복됨.
    // 이렇게 되면 큰일나는 것이다.
    // 이럴땐 어떻게 해결하냐, (양방향 연관관계일때)
    // 한 쪽에는 @JsonIgnore 를 달아줘야 함.

    // 그리고 두 번재 문제.
    // Order 에 가면 Member 라는 필드를 가지고 있는데, 이 Member 는 fetch 가 LAZY, 지연로딩이다.
    // Order 를 만들더라도, 실제 DB 에서 Member 를 조회하진 않는다.
    // 근데 그렇다고 member = null; 로 둘 수는 없으니 proxy 버전의 member. 임시 member 를 만들어주는데,
    // private Member member = new ByteBuddyInterceptor();
    // 이 코드가 그런 과정을 가능하게 해주지만, 우리 눈에 보이지는 않는다.
    // 그냥 프록시 기술이라는게 그런걸 가능하게 해준다. LAZY 설정을 해놓으면 이런 과정이 안보이는데서 이루어지는구나 라고 이해.
    // 그리고 이제 Member 에서 뭔가를 꺼내려고. member.get 머시기를 할 때 비로소 Member 를 조회해서 꺼내온다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // 원하는 정보만 꺼내볼 수 있는 방법. 물론 FORCE_LAZY_LOADING 은 꺼야함.
        for (Order order : all){
            order.getMember().getName(); // order.getMember() 까진 프록시. 가짜 엔티티고, getName() 을 해야 진짜로 쿼리날려서 가져옴.
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    // 로직 자체는 V2 와 같지만, 실행되는 쿼리 수가 다르다. 성능이 더 좋다.
    // findAllWithMemberDelivery 를 그렇게 만들어놓았음!(패치 조인을 사용한 쿼리문)
    // V3는 엔티티를 DTO 로 변환하는 방식
    @GetMapping("api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
        return result;
    }

    // V4는 DTO 로 바로 조회하는 방식
    // V3 와 V4 는 각각 장단점이 있기 때문에, 상황에 맞추어 사용하는 센스가 필요하다.
    @GetMapping("api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }




}
