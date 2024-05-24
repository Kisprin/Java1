package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Club;
import org.fatmansoft.teach.models.TeachCourse;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TeachCourse 数据操作接口，主要实现TeachCourse数据的查询操作
 */

@Repository
public interface TeachCourseRepository extends JpaRepository<TeachCourse,Integer> {
    @Query(value = "from TeachCourse where ?1='' or num like %?1% or name like %?1% ")
    List<TeachCourse> findTeachCourseListByNumName(String numName);
    @Query(value = "from TeachCourse where ?1='' or num = ?1  ")
    Optional<TeachCourse> findTeachCourseByNum(String num);
    Optional<TeachCourse> findByNum(String num);
    List<TeachCourse> findByName(String name);
}
