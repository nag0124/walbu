package walbu.project.domain.lecture.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.data.dto.CreateLectureResponse;
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

}
