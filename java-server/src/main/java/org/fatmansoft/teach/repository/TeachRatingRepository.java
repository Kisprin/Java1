package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.TeachCourse;
import org.fatmansoft.teach.models.TeachRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * TeachRating 数据操作接口，主要实现TeachRating数据的查询操作
 * List<TeachRating> findByTeacherTeacherId(Integer teacherId);  根据关联的Teacher的teacher_id查询获得List<TeachRating>对象集合,  命名规范
 */

@Repository
public interface TeachRatingRepository extends JpaRepository<TeachRating,Integer> {
    List<TeachRating> findByTeacherTeacherId(Integer teacherId);
   @Query(value="from TeachRating where (?1=0 or teacher.teacherId=?1) and (?2=0 or teachCourse.teachCourseId=?2)" )
    List<TeachRating> findByTeacherTeachCourse(Integer teacherId, Integer teachCourseId);

    @Query(value="from TeachRating where teacher.teacherId=?1 and (?2=0 or teachCourse.name like %?2%)" )
    List<TeachRating> findByTeacherTeachCourse(Integer teacherId, String teachCourseName);

}
