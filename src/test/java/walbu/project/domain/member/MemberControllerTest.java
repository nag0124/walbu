package walbu.project.domain.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import walbu.project.domain.member.controller.MemberController;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.service.MemberService;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입에 성공하면 200 응답을 보낸다.")
    void createProduct() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );

        // when & then
        mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 가입 Request의 이름 유효성 검증에 실패하면 400 응답을 보낸다.")
    void createInvalidNameMember() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );

        // when & then
        mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 Request의 이메일 유효성 검증에 실패하면 400 응답을 보낸다.")
    void createInvalidEmailMember() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "nag",
                "nag",
                "1q2w3e4r!",
                "01012341234",
                MemberType.STUDENT
        );

        // when & then
        mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 Request의 핸드폰 번호 유효성 검증에 실패하면 400 응답을 보낸다.")
    void createInvalidPhoneNumberMember() throws Exception {
        // given
        CreateMemberRequest request = new CreateMemberRequest(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "0",
                MemberType.STUDENT
        );

        // when & then
        mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 Request의 회원 타입 유효성 검증에 실패하면 400 응답을 보낸다.")
    void createInvalidTypeMember() throws Exception {
        // given
        Map<String, String> request = Map.of(
                "name", "nage",
                "email", "nag@walbu.com",
                "password", "1q2w3e4r!",
                "phoneNumber", "01012341234",
                "type", "admin"
        );

        // when & then
        mockMvc.perform(
                        post("/api/members")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
