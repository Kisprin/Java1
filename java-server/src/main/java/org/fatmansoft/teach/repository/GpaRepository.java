package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Gpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface GpaRepository extends JpaRepository<Gpa,Integer> {
    @Query(value = "select max(gpaId) from Gpa ")
    Integer getMaxId();

    @Query(value = "from Gpa where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Gpa> findGpaListByNumName(String numName);

    @Query(value = "from Gpa where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and gpa like %?2% )")
    List<Gpa> findGpaListByNumNameAndQuery(String numName, String query);

    List<Gpa> findByStudentPersonNum(String num);
}
