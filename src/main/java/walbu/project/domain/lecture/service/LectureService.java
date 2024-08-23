package walbu.project.domain.lecture.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import walbu.project.common.error.exception.MemberNotFoundException;
import walbu.project.common.error.exception.SameNameLectureExistsException;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.data.dto.CreateLectureResponse;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreateLectureResponse createLecture(CreateLectureRequest request) {
        checkNameExists(request.getName());

        Member instructor = memberRepository.findById(request.getInstructorId()).orElseThrow(MemberNotFoundException::new);
        Lecture lecture = request.toLecture(instructor);

        lectureRepository.save(lecture);
        return CreateLectureResponse.from(lecture);
    }

    private void checkNameExists(String name) {
        if (lectureRepository.existsByName(name)) {
            throw new SameNameLectureExistsException();
        }
    }

}
