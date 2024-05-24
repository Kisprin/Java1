package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Log;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log,Integer> {
    @Query(value = "select max(logId) from Log  ")
    Integer getMaxId();
    @Query(value = "from Log where ?1=''  ")
    List<Log> findLogListByNumName(String numName);
    List<Log> findByType(String type);
    @Query(value = "from Log where type like %?1% and (student.person.name like %?2% or student.person.num like %?2%)")
    List<Log> findStudentListByTypeAndNameNum(String type,String num);
    @Query(value = "from Log where type like %?1% and ((student.person.name like %?2% or student.person.num like %?2%) and (beginTime like %?3% or endTime like %?3%))")
    List findStudentListByTypeAndNameNumAndTime(String typeName, String num, String time);

    List<Log> findByStudentPersonNum(String num);
}

