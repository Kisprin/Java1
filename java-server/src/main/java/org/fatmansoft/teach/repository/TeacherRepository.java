package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
/**
 * Teacher 数据操作接口，主要实现Person数据的查询操作
 * Integer getMaxId()  Teacher 表中的最大的Teacher_id;    JPQL 注解
 * Optional<Teacher> findByPersonPersonId(Integer personId); 根据关联的Person的personId查询获得Option<Teacher>对象 命名规范
 * Optional<Teacher> findByPersonNum(String num); 根据关联的Person的num查询获得Option<Teacher>对象  命名规范
 * List<Teacher> findByPersonName(String name); 根据关联的Person的name查询获得List<Teacher>对象集合  可能存在相同姓名的多个学生 命名规范
 * List<Teacher> findTeacherListByNumName(String numName); 根据输入的参数 如果参数为空，查询所有的学生，输入参数不为空，查询学号或姓名包含输入参数串的所有学生集合
 */

public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    Optional<Teacher> findByPersonPersonId(Integer personId);
    Optional<Teacher> findByPersonNum(String num);
    List<Teacher> findByPersonName(String name);

    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);

    @Query(value="select s from Teacher s, User u where u.person.personId = s.person.personId and u.userId=?1")
    Optional<Teacher> findByUserId(Integer userId);

    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ",
            countQuery = "SELECT count(teacherId) from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    Page<Teacher> findTeacherPageByNumName(String numName,  Pageable pageable);
}
