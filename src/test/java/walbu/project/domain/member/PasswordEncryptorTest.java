package walbu.project.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import walbu.project.domain.member.service.PasswordEncryptor;

public class PasswordEncryptorTest {

    private final PasswordEncryptor encryptor = new PasswordEncryptor();

    @Test
    @DisplayName("비밀번호가 암호화 되면 원래 비밀번호와 다르다.")
    void encryptPassword() throws NoSuchAlgorithmException {
        // given
        String password = "1q2w3e4r!";

        // when
        String encrypted = encryptor.encrypt(password);

        // then
        assertThat(encrypted).isNotEqualTo(password);
    }

    @Test
    @DisplayName("같은 비밀번호를 암호화하면 같은 값을 가진다.")
    void encryptSamePasswords() throws NoSuchAlgorithmException {
        // given
        String password1 = "1q2w3e4r!";
        String password2 = "1q2w3e4r!";

        // when
        String encrypted1 = encryptor.encrypt(password1);
        String encrypted2 = encryptor.encrypt(password2);

        // then
        assertThat(encrypted1).isEqualTo(encrypted2);
    }

}
