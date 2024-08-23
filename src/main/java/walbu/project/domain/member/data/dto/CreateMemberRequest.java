package walbu.project.domain.member.data.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;

@RequiredArgsConstructor
@Getter
public class CreateMemberRequest {

    private final String name;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final MemberType type;

    public Member toMember() {
        return new Member(
                name,
                email,
                password,
                phoneNumber,
                type
        );
    }

}
