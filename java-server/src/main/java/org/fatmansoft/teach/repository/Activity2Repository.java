package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface Activity2Repository extends JpaRepository<Activity2,Integer> {
    @Query(value = "select max(activity2Id) from Activity2 ")
    Integer getMaxId();

    @Query(value = "from Activity2 where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Activity2> findActivity2ListByNumName(String numName);

    @Query(value = "from Activity2 where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and activity2 like %?2% )")
    List<Activity2> findActivity2ListByNumNameAndQuery(String numName, String query);

    List<Activity2> findByStudentPersonNum(String num);
}
