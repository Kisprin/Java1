package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
/**
 * Salary 数据操作接口，主要实现Person数据的查询操作
 * Integer getMaxId()  Salary 表中的最大的salary_id;    JPQL 注解
 * Optional<Salary> findByTeacherIdAndDay(Integer teacherId, String day);  根据teacher_id 和day 查询获得Option<Salary>对象,  命名规范
 * List<Salary> findListByTeacher(Integer teacherId);  查询学生（teacher_id）所有的消费记录  JPQL 注解
 */
public interface SalaryRepository extends JpaRepository<Salary,Integer> {

    Optional<Salary> findByTeacherTeacherIdAndDay(Integer teacherId, String day);

    @Query(value= "from Salary where teacher.teacherId=?1 order by day")
    List<Salary> findListByTeacher(Integer teacherId);

    @Query(value = "select sum(money) from Salary where teacher.teacherId=?1 and day like ?2%")
    Double getMoneyByTeacherIdAndDate(Integer teacherId,String date);

}

