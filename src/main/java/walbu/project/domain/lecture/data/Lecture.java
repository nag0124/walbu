package walbu.project.domain.lecture.data;

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
import walbu.project.domain.member.data.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member instructor;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer enrollmentCount;

    public Lecture(Member instructor, String name, Integer price, Integer enrollmentCount) {
        this.instructor = instructor;
        this.name = name;
        this.price = price;
        this.enrollmentCount = enrollmentCount;
    }

}
