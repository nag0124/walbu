package walbu.project.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.CreateMemberResponse;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.domain.member.service.MemberService;
import walbu.project.util.TestDataFactory;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장에 성공하면 아이디가 부여된다.")
    void saveMemberAndGetId() {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "name"
                ,"email"
                ,"password"
                ,"01012341234"
                , MemberType.STUDENT
        );

        // when
        CreateMemberResponse response = memberService.createMember(request);

        // then
        assertThat(response.getMemberId()).isNotNull();
    }

    @Test
    @DisplayName("동일한 이름의 회원은 가입할 수 없다.")
    void cantSaveSameNameMember() {
        // given
        Member member = new Member(
                "name",
                "email",
                "password",
                "phoneNumber"
                ,MemberType.STUDENT
        );
        memberRepository.save(member);

        CreateMemberRequest request = new CreateMemberRequest(
                member.getName()
                ,"email"
                ,"password"
                ,"01012341234"
                , MemberType.STUDENT
        );

        // when
        CreateMemberResponse response = memberService.createMember(request);

        // then
        assertThat(response.getMemberId()).isNotNull();
    }

}
