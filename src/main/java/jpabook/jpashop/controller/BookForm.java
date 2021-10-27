package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    // 상품은 수정 기능이 있는데 이 기능을 이용할때 id가 필요하기 때문에 폼에서 id를 받을거임.
    // 멤버폼에 아이디가 없었던 것은 수정 기능이 없으니깐!
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;
}
