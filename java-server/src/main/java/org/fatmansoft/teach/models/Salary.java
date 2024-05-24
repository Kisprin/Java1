package org.fatmansoft.teach.models;

import javax.persistence.*;
/**
 * Salary 消费流水实体类  保存学生消费流水的基本信息信息，
 * Integer salaryId 消费表 salary 主键 salary_id
 * Integer teacherId  teacher_id 对应teacher 表里面的 teacher_id
 * String day 日期
 * Double money 金额
 */
@Entity
@Table(	name = "salary"
)
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer salaryId;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    private String day;
    private Double money;

    public Integer getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Integer salaryId) {
        this.salaryId = salaryId;
    }


    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
