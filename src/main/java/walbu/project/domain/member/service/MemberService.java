package walbu.project.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.CreateMemberResponse;
import walbu.project.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest request) {
        Member member = request.toMember();

        memberRepository.save(member);
        return CreateMemberResponse.from(member);
    }

}
