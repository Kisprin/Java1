package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "apply",
        uniqueConstraints = {
        })
public class Apply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applyId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String beginTime;
    private String endTime;
    @Size(max=1000)
    private String type;
    @Size(max=1000)
    private String zhu;

    private String reason;
    private String situation;
    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZhu() {
        return zhu;
    }

    public void setZhu(String zhu) {
        this.zhu = zhu;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }
}