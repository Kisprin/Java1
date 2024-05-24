package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Activity2;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.Activity2Repository;
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
@RequestMapping("/api/activity2")

public class Activity2Controller {
    @Autowired
    private Activity2Repository activity2Repository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewActivity2Id(){
        Integer  id = activity2Repository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getActivity2MapList(String numName) {
        List dataList = new ArrayList();
        List<Activity2> sList = activity2Repository.findActivity2ListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromActivity2(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromActivity2(Activity2 h) {
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
        m.put("activity2Id",h.getActivity2Id());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getActivity2List")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getActivity2MapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getActivity2ListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getActivity2MapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getActivity2MapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Activity2> sList = activity2Repository.findActivity2ListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromActivity2(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/activity2Delete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity2Id = dataRequest.getInteger("activity2Id");  //获取student_id值
        Activity2 h= null;
        Optional<Activity2> op;
        if(activity2Id != null) {
            op= activity2Repository.findById(activity2Id);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            activity2Repository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getActivity2Info")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivity2Info(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity2Id = dataRequest.getInteger("activity2Id");
        Activity2 s= null;
        Optional<Activity2> op;
        if(activity2Id != null) {
            op= activity2Repository.findById(activity2Id); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromActivity2(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/activity2EditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse activity2EditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer activity2Id = dataRequest.getInteger("activity2Id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Activity2 h=null;
        Optional<Activity2>op;
        if(activity2Id != null) {
            op= activity2Repository.findById(activity2Id);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Activity2();
            h.setActivity2Id(getNewActivity2Id());
        }
        h.setTime(CommonMethod.getString(form,"time"));
        h.setType(CommonMethod.getString(form,"type"));
        h.setPlace(CommonMethod.getString(form,"place"));
        h.setStudent(s);
        activity2Repository.save(h);
        return CommonMethod.getReturnData(h.getActivity2Id());  //
    }

}


