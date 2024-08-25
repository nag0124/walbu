package walbu.project.domain.lecture.data.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReadLectureResponse {

    private final Long lectureId;
    private final String name;
    private final String instructorName;
    private final Integer price;
    private final Integer assignedCount;
    private final Integer enrollmentCount;

}
