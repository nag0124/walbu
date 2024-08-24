package walbu.project.domain.enrollment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import walbu.project.domain.enrollment.data.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByMemberId(Long memberId);

    List<Enrollment> findAllByLectureId(Long lectureId);

    boolean existsByMemberIdAndLectureId(Long memberId, Long lectureId);

}
