package walbu.project.domain.enrollment;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import walbu.project.domain.enrollment.controller.EnrollmentController;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;
import walbu.project.domain.enrollment.service.EnrollmentService;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTest {

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("수강 신청에 성공하면 전달되면 200 응답을 받는다.")
    void createEnrollment() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                1L,
                1L
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class)))
                .thenReturn(CreateEnrollmentResponse.from(1L, EnrollmentResultType.SUCCESS));

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(EnrollmentResultType.SUCCESS.getMessage()));

    }

    @Test
    @DisplayName("수강 신청에 실패하면 전달되면 409 응답을 받는다.")
    void failToCreateEnrollment() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                1L,
                1L
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class)))
                .thenReturn(CreateEnrollmentResponse.from(1L, EnrollmentResultType.FAIL
                ));

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(EnrollmentResultType.FAIL.getMessage()));
    }

    @Test
    @DisplayName("수강 신청 Request의 Lecture Id는 1보다 작을 수 없다.")
    void createEnrollmentRequestLectureIdIsOverZero() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                1L,
                0L
        );

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강 신청 Request의 Lecture Id는 있어야 한다.")
    void createEnrollmentRequestLectureIdIsNotNull() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                1L,
                null
        );

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강 신청 Request의 Student Id는 1보다 작을 수 없다.")
    void createEnrollmentRequestStudentIdIsOverZero() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                0L,
                1L
        );

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강 신청 Request의 Student Id는 있어야 한다.")
    void createEnrollmentRequestStudentIdIsNotNull() throws Exception {
        // given
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                null,
                1L
        );

        // when & then
        mockMvc.perform(
                        post("/api/enrollments")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
