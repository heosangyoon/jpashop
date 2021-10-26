package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // API 를 만들때는 절대 엔티티를 외부로 리턴하지 마세영.
    // API 는 스펙이다. 패스워드 노출이나, 스펙이 변해버리는 문제가 생길 수 있다.
    // 그건 굉장히 불안정한 API 이고, 이걸 가져다 쓰는 개발자들은 피똥을 싸게된다!
    @GetMapping(value = "/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

    @GetMapping("members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("members/new")
    public String create(@Valid MemberForm form, BindingResult result){
        // Member 와 MemberForm 은 거의 똑같이 생겼는데 Member 를 넣지 않고 MemberForm 을 넣은 이유는?
        // 화면에서 넘어오는 validation 과 실제 도메인이 원하는 validation 이 다를 수 있다 ?
        // 이렇게 매칭이 안되는 상황이 생기다보면 도메인 정의한 자바파일의 코드가 지저분해진다.
        // 때문에 차라리 화면에 딱 들어맞는 폼을 만들고, 그걸로 데이터를 받는게 낫다.
        if (result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
}
