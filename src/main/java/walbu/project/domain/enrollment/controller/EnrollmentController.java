package walbu.project.domain.enrollment.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.service.EnrollmentAsyncManager;
import walbu.project.domain.enrollment.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentAsyncManager enrollmentAsyncManager;

    @PostMapping
    public ResponseEntity<CreateEnrollmentResponse> createEnrollment(@RequestBody @Valid CreateEnrollmentRequest request) {
        CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<CreateEnrollmentResponse>> createEnrollments(
            @RequestBody @Valid List<CreateEnrollmentRequest> requests) {
        List<CreateEnrollmentResponse> responses = enrollmentAsyncManager.createEnrollments(requests);

        return ResponseEntity
                .ok()
                .body(responses);
    }

}
