package walbu.project.domain.enrollment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.enrollment.data.EnrollmentResultType;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentRequest;
import walbu.project.domain.enrollment.data.dto.CreateEnrollmentResponse;

@Component
@RequiredArgsConstructor
public class EnrollmentAsyncManager {

    private final EnrollmentService enrollmentService;

    public List<CreateEnrollmentResponse> createEnrollments(List<CreateEnrollmentRequest> requests) {
        Map<CompletableFuture<CreateEnrollmentResponse>, Long> futureMap = new HashMap<>();

        for (CreateEnrollmentRequest request : requests) {
            CompletableFuture<CreateEnrollmentResponse> future = enrollmentService.createEnrollmentAsync(request);

            futureMap.put(future, request.getLectureId());
        }

        return futureMap.entrySet().stream()
                .map(entry -> getFutureResult(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    private CreateEnrollmentResponse getFutureResult(Future<CreateEnrollmentResponse> future, Long lectureId) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return CreateEnrollmentResponse.from(lectureId, EnrollmentResultType.FAIL);
        }
    }

}
