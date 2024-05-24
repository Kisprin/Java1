package org.fatmansoft.teach.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "activity2",
        uniqueConstraints = {
        })
public class Activity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activity2Id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    private String time;
    @Size(max = 1000)
    private String type;
    @Size(max = 1000)
    private String place;

    public Integer getActivity2Id() {
        return activity2Id;
    }

    public void setActivity2Id(Integer activity2Id) {
        this.activity2Id = activity2Id;
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
