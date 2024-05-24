package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Outlearn;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.OutlearnRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/outlearn")

public class OutlearnController {
    @Autowired
    private OutlearnRepository outlearnRepository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewOutlearnId(){
        Integer  id = outlearnRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getOutlearnMapList(String numName) {
        List dataList = new ArrayList();
        List<Outlearn> sList = outlearnRepository.findOutlearnListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromOutlearn(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromOutlearn(Outlearn h) {
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
        m.put("reason",h.getReason());
        m.put("se",h.getSe());
        m.put("time",h.getTime());
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("outlearnId",h.getOutlearnId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getOutlearnList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getOutlearnMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getOutlearnListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getOutlearnMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getOutlearnMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Outlearn> sList = outlearnRepository.findOutlearnListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromOutlearn(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/outlearnDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer outlearnId = dataRequest.getInteger("outlearnId");  //获取student_id值
        Outlearn h= null;
        Optional<Outlearn> op;
        if(outlearnId != null) {
            op= outlearnRepository.findById(outlearnId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            outlearnRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getOutlearnInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getOutlearnInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer outlearnId = dataRequest.getInteger("outlearnId");
        Outlearn s= null;
        Optional<Outlearn> op;
        if(outlearnId != null) {
            op= outlearnRepository.findById(outlearnId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromOutlearn(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/outlearnEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse outlearnEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer outlearnId = dataRequest.getInteger("outlearnId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Outlearn h=null;
        Optional<Outlearn>op;
        if(outlearnId != null) {
            op= outlearnRepository.findById(outlearnId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        Optional<Student> nOp = studentRepository.findByPersonNum(num); //查询是否存在num的人员
        if(!nOp.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        Student s=nOp.get();
        if(h == null) {
            h=new Outlearn();
            h.setOutlearnId(getNewOutlearnId());
        }
        h.setTime(CommonMethod.getString(form,"time"));
        h.setDept(CommonMethod.getString(form,"dept"));
        h.setMajor(CommonMethod.getString(form,"major"));
        h.setClassName(CommonMethod.getString(form,"className"));
        h.setCard(CommonMethod.getString(form,"card"));
        h.setGender(CommonMethod.getString(form,"gender"));
        h.setReason(CommonMethod.getString(form,"reason"));
        h.setSe(CommonMethod.getString(form,"se"));
        h.setStudent(s);
        outlearnRepository.save(h);
        return CommonMethod.getReturnData(h.getOutlearnId());  //
    }

}


