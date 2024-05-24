package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TechnologyRepository extends JpaRepository<Technology,Integer> {
    @Query(value = "select max(technologyId) from Technology ")
    Integer getMaxId();

    @Query(value = "from Technology where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Technology> findTechnologyListByNumName(String numName);

    @Query(value = "from Technology where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and technology like %?2% )")
    List<Technology> findTechnologyListByNumNameAndQuery(String numName, String query);

    List<Technology> findByStudentPersonNum(String num);
}
