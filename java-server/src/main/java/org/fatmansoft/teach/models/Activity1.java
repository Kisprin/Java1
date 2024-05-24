package org.fatmansoft.teach.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "activity1",
        uniqueConstraints = {
        })
public class Activity1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activity1Id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    private String time;
    @Size(max = 1000)
    private String type;
    @Size(max = 1000)
    private String place;

    public Integer getActivity1Id() {
        return activity1Id;
    }

    public void setActivity1Id(Integer activity1Id) {
        this.activity1Id = activity1Id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
