package walbu.project.domain.enrollment.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walbu.project.domain.lecture.data.Lecture;
import walbu.project.domain.member.data.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    public Enrollment(Member member, Lecture lecture) {
        this.member = member;
        this.lecture = lecture;
    }

}
