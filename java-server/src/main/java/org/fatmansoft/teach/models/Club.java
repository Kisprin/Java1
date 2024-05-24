package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */
//@Entity注解是JPA（Java Persistence API）中的一个注解，用于将Java类映射到数据库中的表。通过在实体类上
// 添加@Entity注解，可以告诉JPA这个类是一个实体类，需要映射到数据库中的表。@Entity注解通常与@Id注解一起使用，
// @Id注解用于指定实体类的主键。     苏
@Entity
@Table(	name = "club",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "clubNum"),   //人员表中的编号 唯一
        })
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clubId;
    @NotBlank
    @Size(max = 20)
    private String clubNum;

    @Size(max = 50)
    private String clubName;

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public String getClubNum() {
        return clubNum;
    }

    public void setClubNum(String clubNum) {
        this.clubNum = clubNum;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
}
