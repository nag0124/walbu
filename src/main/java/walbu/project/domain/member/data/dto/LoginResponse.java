package walbu.project.domain.member.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;

@RequiredArgsConstructor
@Getter
public class LoginResponse {

    private final Long memberId;

    public static LoginResponse from(Member member) {
        return new LoginResponse(member.getId());
    };

}
