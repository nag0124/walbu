package walbu.project.domain.lecture.data.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import walbu.project.domain.lecture.data.LectureOrderByType;

@RequiredArgsConstructor
@Getter
public class ReadLecturePage {

    private final List<ReadLectureResponse> lectures;
    private final int page;
    private final int size;
    private final LectureOrderByType sort;
    private final int totalPages;
    private final long totalElements;

    public static ReadLecturePage from(Page<ReadLectureResponse> responses, LectureOrderByType orderType) {
        return new ReadLecturePage(
                responses.getContent(),
                responses.getNumber(),
                responses.getSize(),
                orderType,
                responses.getTotalPages(),
                responses.getTotalElements()
        );
    }

}
