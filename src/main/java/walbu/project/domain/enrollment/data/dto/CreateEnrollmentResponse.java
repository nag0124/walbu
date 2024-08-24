package walbu.project.domain.enrollment.data.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.lecture.data.Lecture;

@Getter
public class CreateEnrollmentResponse {

    private final HttpStatus status;
    private final Long lectureId;
    private final String message;

    private CreateEnrollmentResponse(HttpStatus status, Long lectureId, String message) {
        this.status = status;
        this.lectureId = lectureId;
        this.message = message;
    }

    public static CreateEnrollmentResponse from(Lecture lecture, EnrollmentResultType type) {
        return new CreateEnrollmentResponse(
                type.getStatus(),
                lecture.getId(),
                type.getMessage()
        );

    }

}
