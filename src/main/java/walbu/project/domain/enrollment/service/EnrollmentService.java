package walbu.project.domain.enrollment.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import walbu.project.common.error.exception.InstructorCantEnrollHisLectureException;
import walbu.project.common.error.exception.LectureNotFoundException;
import walbu.project.common.error.exception.MemberEnrolledLectureException;
import walbu.project.common.error.exception.MemberNotFoundException;
import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.repository.EnrollmentRepository;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public CreateEnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        checkStudentIsInstructor(request);

        Lecture lecture = lectureRepository.findByIdWithPessimisticLock(request.getLectureId())
                .orElseThrow(LectureNotFoundException::new);
        checkEnrollmentExists(request);

        Member student = memberRepository.findById(request.getStudentId()).orElseThrow(MemberNotFoundException::new);

        if (!lecture.assignSeat()) {
            return CreateEnrollmentResponse.from(lecture.getId(), EnrollmentResultType.FAIL);
        }
        Enrollment enrollment = new Enrollment(student, lecture);
        enrollmentRepository.save(enrollment);
        return CreateEnrollmentResponse.from(enrollment.getLecture().getId(), EnrollmentResultType.SUCCESS);
    }

    @Transactional
    @Async
    public CompletableFuture<CreateEnrollmentResponse> createEnrollmentAsync(CreateEnrollmentRequest request) {
        CreateEnrollmentResponse response = createEnrollment(request);
        return CompletableFuture.completedFuture(response);
    }

    private void checkEnrollmentExists(CreateEnrollmentRequest request) {
        if (enrollmentRepository.existsByMemberIdAndLectureId(request.getStudentId(), request.getLectureId())) {
            throw new MemberEnrolledLectureException();
        }
    }

    private void checkStudentIsInstructor(CreateEnrollmentRequest request) {
        if (lectureRepository.existsByIdAndInstructorId(request.getLectureId(), request.getStudentId())) {
            throw new InstructorCantEnrollHisLectureException();
        }
    }

}
