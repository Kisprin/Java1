package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Activity1;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.Activity1Repository;
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
@RequestMapping("/api/activity1")

public class Activity1Controller {
    @Autowired
    private Activity1Repository activity1Repository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewActivity1Id(){
        Integer  id = activity1Repository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getActivity1MapList(String numName) {
        List dataList = new ArrayList();
        List<Activity1> sList = activity1Repository.findActivity1ListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromActivity1(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromActivity1(Activity1 h) {
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
        m.put("time",h.getTime());
        m.put("type",h.getType());
        m.put("place",h.getPlace());
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("activity1Id",h.getActivity1Id());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getActivity1List")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getActivity1MapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getActivity1ListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getActivity1MapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getActivity1MapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Activity1> sList = activity1Repository.findActivity1ListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromActivity1(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/activity1Delete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity1Id = dataRequest.getInteger("activity1Id");  //获取student_id值
        Activity1 h= null;
        Optional<Activity1> op;
        if(activity1Id != null) {
            op= activity1Repository.findById(activity1Id);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            activity1Repository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getActivity1Info")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivity1Info(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity1Id = dataRequest.getInteger("activity1Id");
        Activity1 s= null;
        Optional<Activity1> op;
        if(activity1Id != null) {
            op= activity1Repository.findById(activity1Id); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromActivity1(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/activity1EditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse activity1EditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity1Id = dataRequest.getInteger("activity1Id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Activity1 h=null;
        Optional<Activity1>op;
        if(activity1Id != null) {
            op= activity1Repository.findById(activity1Id);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Activity1();
            h.setActivity1Id(getNewActivity1Id());
        }
        h.setTime(CommonMethod.getString(form,"time"));
        h.setType(CommonMethod.getString(form,"type"));
        h.setPlace(CommonMethod.getString(form,"place"));
        h.setStudent(s);
        activity1Repository.save(h);
        return CommonMethod.getReturnData(h.getActivity1Id());  //
    }

}


