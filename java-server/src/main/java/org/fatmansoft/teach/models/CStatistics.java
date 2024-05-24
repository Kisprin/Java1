package org.fatmansoft.teach.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(	name = "cStatistics",
        uniqueConstraints = {
        })
public class CStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cStatisticsId;
    @OneToMany(mappedBy = "cStatistics", cascade = CascadeType.ALL)
    private List<Clocking> clockings;
    private Integer num;
    private Integer percent;

    public Integer getcStatisticsId() {
        return cStatisticsId;
    }

    public void setcStatisticsId(Integer cStatisticsId) {
        this.cStatisticsId = cStatisticsId;
    }

    public List<Clocking> getClockings() {
        return clockings;
    }

    public void setClockings(List<Clocking> clockings) {
        this.clockings = clockings;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}

