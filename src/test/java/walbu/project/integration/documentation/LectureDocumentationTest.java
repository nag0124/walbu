package walbu.project.integration.documentation;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import walbu.project.IntegrationTest;
import walbu.project.common.jwt.JwtProvider;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.lecture.data.dto.CreateLectureRequest;
import walbu.project.domain.lecture.repository.LectureRepository;
import walbu.project.domain.member.data.Member;
import walbu.project.domain.member.data.MemberType;
import walbu.project.domain.member.repository.MemberRepository;
import walbu.project.util.TestDataFactory;

public class LectureDocumentationTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Test
    @DisplayName("강의를 생성한다.")
    void createLecture() {
        // given
        Member member = new Member(
                "nag",
                "nag@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(member);
        String token = jwtProvider.createToken(member.getId());

        CreateLectureRequest request = new CreateLectureRequest(
                member.getId(),
                "나그와 함께하는 부동산",
                10000,
                10
        );

        // when & then
        RestAssured
                .given(this.spec).log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("instructorId").type(JsonFieldType.NUMBER).description("강사 아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("강의 이름")
                                        .attributes(key("format").value("중복 불가")),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("강의 가격"),
                                fieldWithPath("enrollmentCount").type(JsonFieldType.NUMBER).description("강의 총 인원")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("개설한 강의 아이디")
                        )))
                .when()
                .post("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectureId", notNullValue());
    }

    @Test
    @DisplayName("Lecture를 정렬하여 가져온다.")
    void readOrderedLectures() {
        // given
        Member instructor = new Member(
                "instructor",
                "instructor@walbu.com",
                "1q2w3e4r!",
                "01012341234",
                MemberType.INSTRUCTOR
        );
        memberRepository.save(instructor);

        List<Lecture> lectures = TestDataFactory.createLectures(instructor, 20);
        makeAssignCountDuplicate(lectures);
        lectureRepository.saveAll(lectures);

        // when & then
        Response response = RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .filter(document("{class-name}/{method-name}",
                        requestParts(
                                partWithName("page").description("조회할 페이지 (기본값: 0)").optional(),
                                partWithName("size").description("조회할 크기 (기본값: 20)").optional(),
                                partWithName("sort").description("정렬 방식 +\n종류: createdTime(최신 등록순), +\nassignedCount(등록자 많은순), +\nenrollmentRate(등록률 높은 순) +\n기본값: createdTime").optional()
                        ),
                        responseFields(
                                fieldWithPath("lectures[].lectureId").type(JsonFieldType.NUMBER).description("강의 아이디"),
                                fieldWithPath("lectures[].name").type(JsonFieldType.STRING).description("강의 이름"),
                                fieldWithPath("lectures[].instructorName").type(JsonFieldType.STRING).description("강사 이름"),
                                fieldWithPath("lectures[].price").type(JsonFieldType.NUMBER).description("강의 가격"),
                                fieldWithPath("lectures[].assignedCount").type(JsonFieldType.NUMBER).description("수강 가능 인원"),
                                fieldWithPath("lectures[].enrollmentCount").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("조회한 사이즈"),
                                fieldWithPath("sort").type(JsonFieldType.STRING).description("정렬 방식"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 요소 개수")
                        )))
                .when()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("sort", "assignedCount")
                .get("/api/lectures")
                .then().log().all()
                .statusCode(200)
                .body("lectures.size()", is(20))
                .extract().response();

        List<Integer> lectureIds = response.jsonPath().getList("lectures.lectureId");
        for (int i = 0; i < lectureIds.size() - 1; i++) {
            assertThat(lectureIds.get(i)).isGreaterThan(lectureIds.get(i + 1));
        }
    }

    private void makeAssignCountDuplicate(List<Lecture> lectures) {
        for (int i = lectures.size() - 1; i >= 0; i--) {
            Lecture lecture = lectures.get(i);
            for (int j = 0; j <= i / 2; j++) {
                lecture.assignSeat();
            }
        }
    }

}
