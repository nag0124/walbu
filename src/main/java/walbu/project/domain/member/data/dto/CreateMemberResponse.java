package walbu.project.domain.member.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;

@RequiredArgsConstructor
@Getter
public class CreateMemberResponse {

    private final Long memberId;

    public static CreateMemberResponse from(Member member) {
        return new CreateMemberResponse(member.getId());
    }

}
