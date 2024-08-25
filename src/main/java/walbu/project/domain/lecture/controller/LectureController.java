package walbu.project.domain.lecture.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.data.dto.CreateLectureResponse;
import walbu.project.domain.lecture.data.dto.ReadLecturePage;
import walbu.project.domain.lecture.data.dto.ReadLectureResponse;
import walbu.project.domain.lecture.service.LectureService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    @PostMapping
    public ResponseEntity<CreateLectureResponse> createLecture(@RequestBody @Valid CreateLectureRequest request) {
        CreateLectureResponse response = lectureService.createLecture(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ReadLecturePage> readLectures(
            @PageableDefault(page = 0, size = 20, sort = "createdTime") Pageable pageable
    ) {
        ReadLecturePage page = lectureService.readLectures(pageable);

        return ResponseEntity
                .ok()
                .body(page);
    }

}
