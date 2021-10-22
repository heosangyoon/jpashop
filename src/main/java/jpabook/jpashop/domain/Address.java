package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address(){
    }

    // Setter 를 모두 제거하고, 생성자에서만 값을 정해주고, 변경 불가능한 클래스를 만듦.
    // JPA 스펙상, 엔티티 혹은 임베디드 타입은 기본 생성자를 두어야하는데,
    // JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 지원해야하기 때문이다.
    // 기본 생성자는 public protected 로 둘 수 있는데, protected 가 그나마 더 안전하다.
    public Address(String city, String street, String zipcode){
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
