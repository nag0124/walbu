package walbu.project.domain.member.service;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import walbu.project.common.error.exception.CantFindEncryptionAlgorithmException;
import walbu.project.common.error.exception.MemberNotFoundException;
import walbu.project.common.error.exception.PasswordIsDifferentException;
import walbu.project.common.error.exception.SameNameMemberExistsException;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.dto.CreateMemberRequest;
import walbu.project.domain.member.data.dto.CreateMemberResponse;
import walbu.project.domain.member.data.dto.LoginRequest;
import walbu.project.domain.member.data.dto.LoginResponse;
import walbu.project.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.existsByName(request.getName())) {
            throw new SameNameMemberExistsException();
        }

        String encryptedPassword = encryptPassword(request.getPassword());
        Member member = request.toMember(encryptedPassword);

        memberRepository.save(member);
        return CreateMemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByName(request.getName()).orElseThrow(MemberNotFoundException::new);
        String encrypt = encryptPassword(request.getPassword());

        if (!member.getPassword().equals(encrypt)) {
            throw new PasswordIsDifferentException();
        }
        return LoginResponse.from(member);
    }

    private String encryptPassword(String password) {
        try {
            return passwordEncryptor.encrypt(password);
        } catch (NoSuchAlgorithmException e) {
            throw new CantFindEncryptionAlgorithmException();
        }
    }

}
