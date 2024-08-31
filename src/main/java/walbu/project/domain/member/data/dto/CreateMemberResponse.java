package walbu.project.domain.member.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;

@RequiredArgsConstructor
@Getter
public class CreateMemberResponse {

    private final Long memberId;
    private final String token;

    public static CreateMemberResponse from(Member member, String token) {
        return new CreateMemberResponse(member.getId(), token);
    }

}
