package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Compete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CompeteRepository extends JpaRepository<Compete,Integer> {
    @Query(value = "select max(competeId) from Compete ")
    Integer getMaxId();

    @Query(value = "from Compete where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Compete> findCompeteListByNumName(String numName);

    @Query(value = "from Compete where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and compete like %?2% )")
    List<Compete> findCompeteListByNumNameAndQuery(String numName, String query);

    List<Compete> findByStudentPersonNum(String num);
}
