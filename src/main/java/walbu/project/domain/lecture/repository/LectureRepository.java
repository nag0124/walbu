package walbu.project.domain.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import walbu.project.domain.lecture.data.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

}
