package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.TeachCourse;
import org.fatmansoft.teach.models.TeachRating;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.TeachCourseRepository;
import org.fatmansoft.teach.repository.TeachRatingRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teachRating")
public class TeachRatingController {
    @Autowired
    private TeachCourseRepository teachCourseRepository;
    @Autowired
    private TeachRatingRepository teachRatingRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Teacher> sList = teacherRepository.findTeacherListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Teacher s : sList) {
            itemList.add(new OptionItem( s.getTeacherId(),s.getTeacherId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getTeachCourseItemOptionList")
    public OptionItemList getTeachCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<TeachCourse> sList = teachCourseRepository.findAll();  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (TeachCourse c : sList) {
            itemList.add(new OptionItem(c.getTeachCourseId(),c.getTeachCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getTeachRatingList")
    public DataResponse getTeachRatingList(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        if(teacherId == null)
            teacherId = 0;
        Integer teachCourseId = dataRequest.getInteger("teachCourseId");
        if(teachCourseId == null)
            teachCourseId = 0;
        List<TeachRating> sList = teachRatingRepository.findByTeacherTeachCourse(teacherId, teachCourseId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (TeachRating s : sList) {
            m = new HashMap();
            m.put("teachRatingId", s.getTeachRatingId()+"");
            m.put("teacherId",s.getTeacher().getTeacherId()+"");
            m.put("teachCourseId",s.getTeachCourse().getTeachCourseId()+"");
            m.put("teacherNum",s.getTeacher().getPerson().getNum());
            m.put("teacherName",s.getTeacher().getPerson().getName());
            m.put("className",s.getTeacher().getDegree());
            m.put("teachCourseNum",s.getTeachCourse().getNum());
            m.put("teachCourseName",s.getTeachCourse().getName());
            m.put("credit",""+s.getTeachCourse().getCredit());
            m.put("mark",""+s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/teachRatingSave")
    public DataResponse teachRatingSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer teachCourseId = dataRequest.getInteger("teachCourseId");
        Integer mark = dataRequest.getInteger("mark");
        Integer teachRatingId = dataRequest.getInteger("teachRatingId");
        Optional<TeachRating> op;
        TeachRating s = null;
        if(teachRatingId != null) {
            op= teachRatingRepository.findById(teachRatingId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            s = new TeachRating();
            s.setTeacher(teacherRepository.findById(teacherId).get());
            s.setTeachCourse(teachCourseRepository.findById(teachCourseId).get());
        }
        s.setMark(mark);
        teachRatingRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/teachRatingDelete")
    public DataResponse teachRatingDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer teachRatingId = dataRequest.getInteger("teachRatingId");
        Optional<TeachRating> op;
        TeachRating s = null;
        if(teachRatingId != null) {
            op= teachRatingRepository.findById(teachRatingId);
            if(op.isPresent()) {
                s = op.get();
                teachRatingRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
