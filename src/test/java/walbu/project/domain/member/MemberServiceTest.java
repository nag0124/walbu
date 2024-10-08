package walbu.project.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import walbu.project.common.error.exception.MemberNotFoundException;
import walbu.project.common.error.exception.PasswordIsDifferentException;
import walbu.project.common.error.exception.SameNameMemberExistsException;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.CreateMemberResponse;
import walbu.project.domain.member.data.dto.LoginRequest;
import walbu.project.domain.member.data.dto.LoginResponse;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.domain.member.service.MemberService;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void cleanUp() {
        memberRepository.deleteAllInBatch();
    }

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

        SameNameMemberExistsException exception = new SameNameMemberExistsException();

        // when & then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(SameNameMemberExistsException.class)
                .hasMessage(exception.getMessage());
    }

    @Test
    @DisplayName(" 회원 가입하면 비밀번호가 암호화된다.")
    void signingUpEncryptsPassword() {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "name",
                "email",
                "password",
                "01012341234",
                MemberType.STUDENT
        );

        // when
        CreateMemberResponse response = memberService.createMember(request);

        // then
        Member member = memberRepository.findById(response.getMemberId()).orElseThrow(MemberNotFoundException::new);
        assertThat(member.getPassword()).isNotEqualTo(request.getPassword());
    }

    @Test
    @DisplayName("로그인 한다.")
    void login() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                "name"
                ,"email"
                ,"password"
                ,"01012341234"
                , MemberType.STUDENT
        );
        Long memberId = memberService.createMember(createMemberRequest).getMemberId();

        LoginRequest request = new LoginRequest("name", "password");

        // when
        LoginResponse response = memberService.login(request);

        // then
        assertThat(response.getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("로그인 요청에서 이름이 DB에 없으면 예외가 발생한다.")
    void NoMemberLoginFails() {
        // given
        LoginRequest request = new LoginRequest("name", "password");

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 요청의 Password가 멤버의 Password와 다르면 예외가 발생한다.")
    void differentPasswordFailsLogin() {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                "name"
                ,"email"
                ,"password"
                ,"01012341234"
                , MemberType.STUDENT
        );
        Long memberId = memberService.createMember(createMemberRequest).getMemberId();

        LoginRequest request = new LoginRequest("name", "password1");

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(PasswordIsDifferentException.class);
    }

}
