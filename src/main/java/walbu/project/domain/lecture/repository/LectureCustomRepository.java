package walbu.project.domain.lecture.repository;

import org.springframework.data.domain.Pageable;

import walbu.project.domain.lecture.data.dto.ReadLecturePage;

public interface LectureCustomRepository {

    ReadLecturePage findPage(Pageable pageable);

}
