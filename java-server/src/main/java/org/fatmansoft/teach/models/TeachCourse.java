package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * TeachCourse 课程表实体类  保存课程的的基本信息信息，
 * Integer teachCourseId 人员表 teachCourse 主键 teachCourse_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * TeachCourse preTeachCourse 前序课程 pre_teachCourse_id 关联前序课程的主键 teachCourse_id
 */
@Entity
@Table(	name = "teachCourse",
        uniqueConstraints = {
        })
public class TeachCourse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teachCourseId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    private Integer credit;
    @ManyToOne
    @JoinColumn(name="pre_teachCourse_id")
    private TeachCourse preTeachCourse;
    @Size(max = 12)
    private String teachCoursePath;

    public Integer getTeachCourseId() {
        return teachCourseId;
    }

    public void setTeachCourseId(Integer teachCourseId) {
        this.teachCourseId = teachCourseId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public TeachCourse getPreTeachCourse() {
        return preTeachCourse;
    }

    public void setPreTeachCourse(TeachCourse preTeachCourse) {
        this.preTeachCourse = preTeachCourse;
    }

    public String getTeachCoursePath() {
        return teachCoursePath;
    }

    public void setTeachCoursePath(String teachCoursePath) {
        this.teachCoursePath = teachCoursePath;
    }
}
