package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.JsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")

public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/courseSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        String name = CommonMethod.getString(form, "name");
        Integer credit = CommonMethod.getInteger(form, "credit");  //Map 获取属性的值
      String preCourse = CommonMethod.getString(form, "preCourse");
        Optional<Course> op;
        Course c = null;
        if (courseId != null) {
            op = courseRepository.findById(courseId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Optional<Course> ncOp = courseRepository.findCourseByNum(num);
        if (ncOp.isPresent()) {
            if (c == null || !c.getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新课程编号已经存在，不能添加或修改！");
            }
        }
        if (c == null)
            c = new Course();
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        if (!preCourse.isEmpty()){
           c.setPreCourse(courseRepository.findCourseByNum(preCourse).get());
        }



        courseRepository.save(c);  //插入新的Person记录

        return CommonMethod.getReturnData(c.getCourseId());  // 将studentId返回前端
    }
    @PostMapping("/courseDelete")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
