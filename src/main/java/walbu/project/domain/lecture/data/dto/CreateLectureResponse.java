package walbu.project.domain.lecture.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.lecture.data.Lecture;

@Getter
public class CreateLectureResponse {

    private final Long lectureId;

    private CreateLectureResponse(Long lectureId) {
        this.lectureId = lectureId;
    }

    public static CreateLectureResponse from(Lecture lecture) {
        return new CreateLectureResponse(lecture.getId());
    }

}
