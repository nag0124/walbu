package walbu.project.domain.lecture.data;

import static walbu.project.domain.lecture.data.QLecture.*;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LectureOrderByType {

    CREATED_DATE("createdDate", new OrderSpecifier[]{lecture.id.desc()}),
    ASSIGNED_COUNT("assignedCount", new OrderSpecifier[]{lecture.assignedCount.desc(), lecture.id.desc()}),
    ENROLLMENT_RATE("enrollmentRate", new OrderSpecifier[]{lecture.enrollmentRate.desc(), lecture.id.desc()});

    private final String sortingField;
    private final OrderSpecifier[] orderSpecifiers;

    public static OrderSpecifier[] getOrderSpecifiersOf(String sortingField) {
        LectureOrderByType type = LectureOrderByType.of(sortingField);

        if (type == null) return CREATED_DATE.orderSpecifiers;
        return type.orderSpecifiers;

    }

    private static LectureOrderByType of(String sortingField) {
        for (LectureOrderByType type : LectureOrderByType.values()) {
            if (type.sortingField.equals(sortingField)) {
                return type;
            }
        }
        return null;
    }


}
