package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Gpa;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.GpaRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/gpa")

public class GpaController {
    @Autowired
    private GpaRepository gpaRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewGpaId(){
        Integer  id = gpaRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getGpaMapList(String numName) {
        List dataList = new ArrayList();
        List<Gpa> sList = gpaRepository.findGpaListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromGpa(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromGpa(Gpa h) {
        Map m = new HashMap();
        if(h == null)
            return m;
        Student s;
        s =h.getStudent();
        if(s == null)
            return m;
        Person p;
        p =s.getPerson();
        if(s == null)
            return m;

        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("gpaId",h.getGpaId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        m.put("g",h.getG());
        return m;
    }



    @PostMapping("/getGpaList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getGpaMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getGpaListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getGpaMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getGpaMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Gpa> sList = gpaRepository.findGpaListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromGpa(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/gpaDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer gpaId = dataRequest.getInteger("gpaId");  //获取student_id值
        Gpa h= null;
        Optional<Gpa> op;
        if(gpaId != null) {
            op= gpaRepository.findById(gpaId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            gpaRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getGpaInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getGpaInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer gpaId = dataRequest.getInteger("gpaId");
        Gpa s= null;
        Optional<Gpa> op;
        if(gpaId != null) {
            op= gpaRepository.findById(gpaId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromGpa(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/gpaEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse gpaEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer gpaId = dataRequest.getInteger("gpaId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Gpa h=null;
        Optional<Gpa>op;
        if(gpaId != null) {
            op= gpaRepository.findById(gpaId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        Optional<Student> nOp = studentRepository.findByPersonNum(num); //查询是否存在num的人员
        List<Score> sco=scoreRepository.findByStudentStudentId(nOp.get().getStudentId());
        int i =sco.size();
        double gpa=0;
        for (int a=0;a<i;a++){
           Score s= sco.get(a);
           gpa+=(s.getMark()-50)/10;
        }gpa/=i;
        if(!nOp.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        Student s=nOp.get();
        if(h == null) {
            h=new Gpa();
            h.setGpaId(getNewGpaId());
        }

        h.setG(gpa);
        h.setStudent(s);
        gpaRepository.save(h);
        return CommonMethod.getReturnData(h.getGpaId());  //
    }

}


