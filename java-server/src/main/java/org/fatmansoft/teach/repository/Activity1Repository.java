package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface Activity1Repository extends JpaRepository<Activity1,Integer> {
    @Query(value = "select max(activity1Id) from Activity1 ")
    Integer getMaxId();

    @Query(value = "from Activity1 where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Activity1> findActivity1ListByNumName(String numName);

    @Query(value = "from Activity1 where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and activity1 like %?2% )")
    List<Activity1> findActivity1ListByNumNameAndQuery(String numName, String query);

    List<Activity1> findByStudentPersonNum(String num);
}
