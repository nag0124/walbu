package walbu.project.domain.lecture.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walbu.project.domain.enrollment.data.Enrollment;
import walbu.project.domain.member.data.Member;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_lecture_assigned_count_id", columnList = "assigned_count, lecture_id"),
                @Index(name = "idx_lecture_enrollment_rate_id", columnList = "enrollment_rate, lecture_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member instructor;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "enrollment_count", nullable = false)
    private Integer enrollmentCount;

    @Column(name = "assigned_count", nullable = false)
    private Integer assignedCount;

    @Column(name = "enrollment_rate", nullable = false)
    private Float enrollmentRate;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    List<Enrollment> enrollments = new ArrayList<>();

    public Lecture(Member instructor, String name, Integer price, Integer enrollmentCount) {
        this.instructor = instructor;
        this.name = name;
        this.price = price;
        this.enrollmentCount = enrollmentCount;
        this.assignedCount = 0;
        this.enrollmentRate = 0f;
    }

    public boolean assignSeat() {
        if (assignedCount >= enrollmentCount) {
            return false;
        }
        assignedCount++;
        enrollmentRate = (float) assignedCount / enrollmentCount;
        return true;
    }

}
