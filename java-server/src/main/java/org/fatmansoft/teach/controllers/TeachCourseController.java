package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.TeachCourse;
import org.fatmansoft.teach.models.TeachCourse;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.TeachCourseRepository;
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
@RequestMapping("/api/teachCourse")

public class TeachCourseController {
    @Autowired
    private TeachCourseRepository teachCourseRepository;
    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/getTeachCourseList")
    public DataResponse getTeachCourseList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<TeachCourse> cList = teachCourseRepository.findTeachCourseListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        TeachCourse pc;
        for (TeachCourse c : cList) {
            m = new HashMap();
            m.put("teachCourseId", c.getTeachCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("teachCoursePath",c.getTeachCoursePath());
            pc =c.getPreTeachCourse();
            if(pc != null) {
                m.put("preTeachCourse",pc.getName());
                m.put("preTeachCourseId",pc.getTeachCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/teachCourseSave")
    public DataResponse teachCourseSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer teachCourseId = dataRequest.getInteger("teachCourseId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        String name = CommonMethod.getString(form, "name");
        Integer credit = CommonMethod.getInteger(form, "credit");  //Map 获取属性的值
        String preTeachCourse = CommonMethod.getString(form, "preTeachCourse");
        Optional<TeachCourse> op;
        TeachCourse c = null;
        if (teachCourseId != null) {
            op = teachCourseRepository.findById(teachCourseId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Optional<TeachCourse> ncOp = teachCourseRepository.findTeachCourseByNum(num);
        if (ncOp.isPresent()) {
            if (c == null || !c.getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新课程编号已经存在，不能添加或修改！");
            }
        }
        if (c == null)
            c = new TeachCourse();
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        if (!preTeachCourse.isEmpty()){
            c.setPreTeachCourse(teachCourseRepository.findTeachCourseByNum(preTeachCourse).get());
        }



        teachCourseRepository.save(c);  //插入新的Person记录

        return CommonMethod.getReturnData(c.getTeachCourseId());  // 将studentId返回前端
    }
    @PostMapping("/teachCourseDelete")
    public DataResponse teachCourseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer teachCourseId = dataRequest.getInteger("teachCourseId");
        Optional<TeachCourse> op;
        TeachCourse c= null;
        if(teachCourseId != null) {
            op = teachCourseRepository.findById(teachCourseId);
            if(op.isPresent()) {
                c = op.get();
                teachCourseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
