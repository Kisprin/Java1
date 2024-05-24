package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.TeacherFamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherFamilyMemberRepository extends JpaRepository<TeacherFamilyMember,Integer> {
    List<TeacherFamilyMember> findByTeacherTeacherId(Integer studentId);
}

