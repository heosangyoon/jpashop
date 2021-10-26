package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class ItemForm {
    @NotEmpty(message = "상품 이름은 필수입니다람쥐")
    private String name;
    private int price;
    private int stockQuantity;

}
