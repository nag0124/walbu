package walbu.project.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import walbu.project.domain.member.data.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
