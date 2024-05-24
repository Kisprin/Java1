package org.fatmansoft.teach.models;

import javax.persistence.*;
/**
 * TeachRating 成绩表实体类  保存成绩的的基本信息信息，
 * Integer teachRatingId 人员表 teachRating 主键 teachRating_id
 * Teacher teacher 关联学生 teacher_id 关联学生的主键 teacher_id
 * TeachCourse teachTeachCourse 关联课程 teachTeachCourse_id 关联课程的主键 teachTeachCourse_id
 * Integer mark 成绩
 * Integer ranking 排名
 */
@Entity
@Table(	name = "teachRating",
        uniqueConstraints = {
        })
public class TeachRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teachRatingId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "teachCourse_id")
    private TeachCourse teachCourse;

    private Integer mark;
    private Integer ranking;


    public Integer getTeachRatingId() {
        return teachRatingId;
    }

    public void setTeachRatingId(Integer teachRatingId) {
        this.teachRatingId = teachRatingId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public TeachCourse getTeachCourse() {
        return teachCourse;
    }

    public void setTeachCourse(TeachCourse teachCourse) {
        this.teachCourse = teachCourse;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
}