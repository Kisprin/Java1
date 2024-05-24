package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.models.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    @Override
    Optional<FamilyMember> findById(Integer integer);

    List<FamilyMember> findByStudentStudentId(Integer studentId);

    @Query(value = "from FamilyMember where ?1='' or name like %?1% or num like %?1% ")
    List<FamilyMember> findFamilyMemberListByNumName(String numName);
    @Query(value = "from FamilyMember where ?1='' or  num like %?1% ")
    Optional<FamilyMember> findFamilyMemberByNum(String num);

}

