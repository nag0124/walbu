package walbu.project.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 엔티티를 저장하면 아이디가 부여된다.")
    void saveMemberAndGetId() {
        // given
        Member member = TestDataFactory.createMember();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("이름이 같은 멤버를 저장하려고 하면 예외가 발생한다.")
    void saveSameNameMembers() {
        // given
        Member member = TestDataFactory.createMember();
        Member sameNameMember = TestDataFactory.createSameNameMember(member);
        memberRepository.save(member);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(sameNameMember))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
