package walbu.project.domain.member.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;

@RequiredArgsConstructor
@Getter
public class LoginResponse {

    private final String token;

    public static LoginResponse from(String token) {
        return new LoginResponse(token);
    }

}
