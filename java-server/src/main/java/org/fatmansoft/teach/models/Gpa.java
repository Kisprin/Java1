package org.fatmansoft.teach.models;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(	name = "gpa",
        uniqueConstraints = {
        })
public class Gpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gpaId;

    @OneToMany(mappedBy = "gpa", cascade = CascadeType.ALL)
    private List<Score> scores;

    @OneToOne
    @JoinColumn(name="student_id")
    @JsonIgnore
    private Student student;

    private Double g;

    public Integer getGpaId() {
        return gpaId;
    }

    public void setGpaId(Integer gpaId) {
        this.gpaId = gpaId;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Double getG() {
        return g;
    }

    public void setG(Double g) {
        this.g = g;
    }
}
