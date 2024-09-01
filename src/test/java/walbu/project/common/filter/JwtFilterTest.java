package walbu.project.common.filter;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import walbu.project.common.error.exception.MalformedTokenException;
import walbu.project.common.error.exception.NoAuthorizationHeaderException;
import walbu.project.common.jwt.JwtProvider;

public class JwtFilterTest {

    @Test
    @DisplayName("JWT가 필터를 통해 검증에 통과한다.")
    void filterWithJwt() throws ServletException, IOException {
        // given
        JwtProvider jwtProvider = new JwtProvider("asdfalsdkfjalksjdflkajsdlfkjasldkfjlskdjflksdjc", 20000L);
        JwtFilter jwtFilter = new JwtFilter(jwtProvider, new ObjectMapper());

        String token = jwtProvider.createToken(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enrollments");
        request.setMethod("POST");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("PostUrlWhiteList의 Post 요청은 JWT가 없어도 필터를 통과한다.")
    void filterPostUrlWhiteList() throws ServletException, IOException {
        // given
        JwtProvider jwtProvider = new JwtProvider("asdfalsdkfjalksjdflkajsdlfkjasldkfjlskdjflksdjc", 20000L);
        JwtFilter jwtFilter = new JwtFilter(jwtProvider, new ObjectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/members/sign-up");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Lecutures의 GET 요청은 JWT가 없어도 필터를 통과한다.")
    void lecturesGetFilter() throws ServletException, IOException {
        // given
        JwtProvider jwtProvider = new JwtProvider("asdfalsdkfjalksjdflkajsdlfkjasldkfjlskdjflksdjc", 20000L);
        JwtFilter jwtFilter = new JwtFilter(jwtProvider, new ObjectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/lectures");
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Authorization Header가 없으면 예외 응답을 반환한다.")
    void filterNoAuthorizationHeaderRequest() throws ServletException, IOException {
        // given
        JwtProvider jwtProvider = new JwtProvider("asdfalsdkfjalksjdflkajsdlfkjasldkfjlskdjflksdjc", 20000L);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtFilter jwtFilter = new JwtFilter(jwtProvider, objectMapper);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enrollments");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();
        NoAuthorizationHeaderException exception = new NoAuthorizationHeaderException();

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);

        Map<String, String> responseMap = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(responseMap.get("message")).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("JwtFilter에서 Token 검증이 실패하면 예외 응답을 반환한다.")
    void filter() throws ServletException, IOException {
        // given
        JwtProvider jwtProvider = new JwtProvider("asdfalsdkfjalksjdflkajsdlfkjasldkfjlskdjflksdjc", 20000L);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtFilter jwtFilter = new JwtFilter(jwtProvider, objectMapper);
        String token = "token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enrollments");
        request.setMethod("POST");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();
        MalformedTokenException exception = new MalformedTokenException();

        // when
        jwtFilter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);

        Map<String, String> responseMap = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(responseMap.get("message")).isEqualTo(exception.getMessage());
    }

}
