package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "modification",
        uniqueConstraints = {
        })
public class Modification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer modificationId;
    @OneToOne
    @JoinColumn(name="person_id")
    private Person person;
    private String type;
    private String time;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String change;

    public Integer getModificationId() {
        return modificationId;
    }

    public void setModificationId(Integer modificationId) {
        this.modificationId = modificationId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
