package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface HonorRepository extends JpaRepository<Honor,Integer> {
    @Query(value = "select max(honorId) from Honor ")
    Integer getMaxId();

    @Query(value = "from Honor where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Honor> findHonorListByNumName(String numName);

    @Query(value = "from Honor where ?1='' or ( (student.person.num like %?1% or student.person.name like %?1% ) and honor like %?2% )")
    List<Honor> findHonorListByNumNameAndQuery(String numName, String query);

    List<Honor> findByStudentPersonNum(String num);
}
