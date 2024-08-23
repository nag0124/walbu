package walbu.project.domain.lecture;

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

import walbu.project.domain.lecture.controller.LectureController;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.service.LectureService;

@WebMvcTest(LectureController.class)
public class LectureControllerTest {

    @MockBean
    private LectureService lectureService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("강의가 생성되면 200 응답을 보낸다.")
    void createProduct() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("강사 아이디는 1보다 작을 수 없다.")
    void requestInstructorIdIsOver0() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                0L,
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강사 아이디는 null일 수 없다.")
    void requestInstructorIdIsNotNull() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                null,
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강의 이름은 빈칸일 수 없다.")
    void requestNameIsNotBlank() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                " ",
                10000,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강 인원은 1보다 작을 수 없다.")
    void requestEnrollmentCountIsOver0() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                10000,
                0
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강 인원은 null일 수 없다.")
    void requestEnrollmentCountIsNotNull() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                10000,
                null
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강의 가격은 0일 수 있다.")
    void requestPriceCanBeZero() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                0,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("강의 가격은 음수일 수 없다.")
    void requestPriceIsNotNegative() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                -1,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강의 가격은 null일 수 없다.")
    void requestPriceIsNotNull() throws Exception {
        // given
        CreateLectureRequest request = new CreateLectureRequest(
                1L,
                "나그와 함께하는 부동산",
                null,
                10
        );

        // when & then
        mockMvc.perform(
                        post("/api/lectures")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
