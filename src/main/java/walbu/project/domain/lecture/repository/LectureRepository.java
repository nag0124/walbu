package walbu.project.domain.lecture.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import walbu.project.domain.lecture.data.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureCustomRepository {

    List<Lecture> findAllByInstructorId(Long instructorId);

    boolean existsByName(String name);

    boolean existsByIdAndInstructorId(Long id, Long instructorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lecture l WHERE l.id = :id")
    Optional<Lecture> findByIdWithPessimisticLock(Long id);


}
