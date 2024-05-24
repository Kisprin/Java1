package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Apply;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply,Integer> {
    @Query(value = "select max(applyId) from Apply ")
    Integer getMaxId();

    @Query(value = "from Apply where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Apply> findApplyListByNumName(String numName);

    @Query(value = "from Apply where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and (beginTime like %?2% or endTime like %?2% or situation like %?2%))")
    List<Apply> findApplyListByNumNameAndQuery(String numName, String query);

    List<Apply> findByStudentPersonNum(String num);
}
