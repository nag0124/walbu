package walbu.project.domain.member.data.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "이메일이 유효하지 않은 형식입니다.")
    private String email;

    private String password;

    @Pattern(regexp = "^01\\d{8,9}$", message = "핸드폰 번호가 유효하지 않은 형식입니다.")
    private String phoneNumber;

    @NotNull(message = "유효하지 않은 멤버 타입입니다.")
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
