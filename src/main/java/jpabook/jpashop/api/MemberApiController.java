package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 이렇게 만든 API 는 어떤 문제가 있냐,
    // 이렇게 엔티티를 직접 노출하게 되면, 의도하지 않은 정보들. 예를 들면 orders. 주문 정보가 후두둑 쏟아질 수 있음.
    // 필드에 @JsonIgnore 라는 어노테이션을 붙일 수도 있지만, 이 엔티티를 사용할 API 가 여러개일텐데, 거기에 일일이 어떻게 다 맞춰줭,,
    // API 마다 주소정보를 가리고 싶을 수도, 주문 정보를 가리고 싶을 수도 있으니깐
    // 결국 엔티티를 직접 리턴하거나 하면 안된다는 결론을 얻게 되고, V2 로 넘어가보자.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().
                map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }


    // JSON 데이터를 member 에다가 쫙 뿌려줌
    // 근데 이런식으로다가 엔티티를 바로 넣어주는 것은 큰 장애의 원인이 되는데,
    // 그 이유는 엔티티와 API 가 1:1 매핑이 되기 때문임.
    // 엔티티라는 것은 굉장히 여러군데서 쓰이기도 하고, 변경도 자주 되는 앤데
    // 변경이 될 때마다 모든 API 로 가서 API 의 스펙 자체를 변경해야하는게 이게 말이 안되는 것임.
    // 실무에서 굉장히 큰 장애의 원인이 될 수 있기 때문에 엔티티를 param 으로 받는 것도 안되고, 그냥 어디 노출시키면 안됨. 절대로 !!!!
    // 엔티티마다 별도의 DTO 클래스를 만들어서 (예를 들면 request 라든지 )요청을 보내는 것이 정석!
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // V2 에 대한 V1 의 유일한 장점은 DTO 클래스를 따로 안 만들어도 된다는 것! 말고는 없다 !
    // 그러나 V2 의 장점은?
    // 누군가 엔티티를 바꾸더라도 API 스펙은 영향을 받지 않는다! -> 안정적인 운영이 가능하다!
    // 그리고 DTO 를 사용하면 엔티티에서 어느 필드까지 피라미터로 넘어오나? 이런걸 알 수 있다?
    // DTO 자체에 어디까지 해줄건지 설명이 되어있기 때문!
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName()); // 커맨드와 쿼리를 분리해라! -> 유지보수성 증대
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }



    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        public CreateMemberResponse(Long id){
            this.id = id;
        }
        private Long id;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
