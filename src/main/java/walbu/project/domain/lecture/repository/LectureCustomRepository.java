package walbu.project.domain.lecture.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import walbu.project.domain.lecture.data.dto.ReadLectureResponse;

public interface LectureCustomRepository {

    Page<ReadLectureResponse> findPage(Pageable pageable);

}
