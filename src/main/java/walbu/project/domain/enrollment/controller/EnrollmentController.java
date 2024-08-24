package walbu.project.domain.enrollment.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<CreateEnrollmentResponse> createEnrollment(@RequestBody @Valid CreateEnrollmentRequest request) {
        CreateEnrollmentResponse response = enrollmentService.createEnrollment(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

}
