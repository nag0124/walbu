package walbu.project.domain.member.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.CreateMemberResponse;
import walbu.project.domain.member.data.dto.LoginRequest;
import walbu.project.domain.member.data.dto.LoginResponse;
import walbu.project.domain.member.service.MemberService;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<CreateMemberResponse> createMember(@RequestBody @Valid CreateMemberRequest request) {
        CreateMemberResponse response = memberService.createMember(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

}
