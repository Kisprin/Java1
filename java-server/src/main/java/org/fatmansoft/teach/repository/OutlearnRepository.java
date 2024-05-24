package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Outlearn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OutlearnRepository extends JpaRepository<Outlearn,Integer> {
    @Query(value = "select max(outlearnId) from Outlearn ")
    Integer getMaxId();

    @Query(value = "from Outlearn where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Outlearn> findOutlearnListByNumName(String numName);

    @Query(value = "from Outlearn where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and outlearn like %?2% )")
    List<Outlearn> findOutlearnListByNumNameAndQuery(String numName, String query);

    List<Outlearn> findByStudentPersonNum(String num);
}
