package walbu.project.domain.lecture.repository;

import static walbu.project.domain.lecture.data.QLecture.*;
import static walbu.project.domain.member.data.QMember.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import walbu.project.domain.lecture.data.LectureOrderByType;
import walbu.project.domain.lecture.data.dto.ReadLecturePage;
import walbu.project.domain.lecture.data.dto.ReadLectureResponse;

@Repository
@RequiredArgsConstructor
public class LectureCustomRepositoryImpl implements LectureCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ReadLecturePage findPage(Pageable pageable) {
        LectureOrderByType orderType = getOrderTypeOf(pageable);

        List<ReadLectureResponse> lectures = jpaQueryFactory
                .select(Projections.constructor(ReadLectureResponse.class,
                        lecture.id,
                        lecture.name,
                        lecture.instructor.name,
                        lecture.price,
                        lecture.assignedCount,
                        lecture.enrollmentCount
                ))
                .from(lecture)
                .innerJoin(lecture.instructor)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderType.getOrderSpecifiers())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(lecture)
                .fetchCount();

        PageImpl page = new PageImpl(lectures, pageable, total);
        return ReadLecturePage.from(page, orderType);
    }

    private LectureOrderByType getOrderTypeOf(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (pageable.getSort() == null || pageable.getSort().isEmpty()) {
            return LectureOrderByType.CREATED_DATE;
        }

        Sort.Order firstOrder = sort.stream().findFirst().orElse(null);
        if (firstOrder == null) {
            return LectureOrderByType.CREATED_DATE;
        }

        String property = firstOrder.getProperty();
        return LectureOrderByType.of(property);
    }

}
