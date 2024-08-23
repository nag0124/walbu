package walbu.project.domain.member.data.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateMemberRequest {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private MemberType type;

    public Member toMember(String encryptedPassword) {
        return new Member(
                name,
                email,
                encryptedPassword,
                phoneNumber,
                type
        );
    }

}
