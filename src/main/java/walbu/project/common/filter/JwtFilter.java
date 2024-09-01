package walbu.project.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.PatternMatchUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import walbu.project.common.error.ErrorResponse;
import walbu.project.common.error.exception.ApiException;
import walbu.project.common.error.exception.BearerPrefixNotIncludedException;
import walbu.project.common.error.exception.MalformedTokenException;
import walbu.project.common.error.exception.NoAuthorizationHeaderException;
import walbu.project.common.error.exception.TokenExpiredException;
import walbu.project.common.error.exception.TokenNotIncludedException;
import walbu.project.common.jwt.JwtProvider;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final String[] postUrlWhiteList = new String[]{"/api/members/sign-up", "/api/members/login"};
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        boolean isTokenValid = false;

        if (checkPostUrlWhiteList(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        if (httpServletRequest.getRequestURI().equals("/api/lectures") && httpServletRequest.getMethod().equals(HttpMethod.GET.name())) {
            chain.doFilter(request, response);
            return;
        }

        isTokenValid = validateToken(httpServletRequest, httpServletResponse);
        if (isTokenValid) {
            chain.doFilter(request, response);
        }
    }

    private boolean checkPostUrlWhiteList(HttpServletRequest request) {
        String url = request.getRequestURI();

        return request.getMethod().equals(HttpMethod.POST.name()) && PatternMatchUtils.simpleMatch(postUrlWhiteList, url);
    }

    private boolean validateToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {
        String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            writeErrorResponse(new NoAuthorizationHeaderException(), httpServletResponse);
            return false;
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            writeErrorResponse(new BearerPrefixNotIncludedException(), httpServletResponse);
            return false;
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        try {
            jwtProvider.validateToken(token);
        } catch (IllegalArgumentException e) {
            writeErrorResponse(new TokenNotIncludedException(), httpServletResponse);
            return false;
        } catch (MalformedJwtException | SecurityException e) {
            writeErrorResponse(new MalformedTokenException(), httpServletResponse);
            return false;
        } catch (ExpiredJwtException e) {
            writeErrorResponse(new TokenExpiredException(), httpServletResponse);
            return false;
        }
        return true;
    }

    private void writeErrorResponse(ApiException exception, HttpServletResponse httpServletResponse)
            throws IOException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ErrorResponse errorResponse = ErrorResponse.from(exception);
        PrintWriter writer = httpServletResponse.getWriter();

        log.error(exception.getClass().getSimpleName() + ": " + exception.getMessage());
        httpServletResponse.setStatus(errorResponse.getStatus().value());
        String jsonBody = objectMapper.writeValueAsString(errorResponse);
        writer.print(jsonBody);
    }

}
