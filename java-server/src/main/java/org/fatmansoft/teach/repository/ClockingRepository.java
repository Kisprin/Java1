package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Clocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ClockingRepository extends JpaRepository<Clocking,Integer> {
    @Query(value = "select max(clockingId) from Clocking ")
    Integer getMaxId();

    @Query(value = "from Clocking where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Clocking> findClockingListByNumName(String numName);

    @Query(value = "from Clocking where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and clocking like %?2% )")
    List<Clocking> findClockingListByNumNameAndQuery(String numName, String query);

    List<Clocking> findByStudentPersonNum(String num);
}
