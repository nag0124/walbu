package walbu.project.common.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JwtProviderTest {

    @Test
    @DisplayName("Member Id로 토큰을 생성한다.")
    void createTokenWithMemberId() {
        // given
        JwtProvider jwtProvider = new JwtProvider("1234567890qwertyuiopasdfghjklzxcvbnm", 0L);
        long memberId = 1L;

        // when
        String token = jwtProvider.createToken(memberId);

        // then
        assertThat(token).isNotNull();
    }

}
