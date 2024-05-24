package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Clocking;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ClockingRepository;
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
@RequestMapping("/api/clocking")

public class ClockingController {
    @Autowired
    private ClockingRepository clockingRepository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewClockingId(){
        Integer  id = clockingRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getClockingMapList(String numName) {
        List dataList = new ArrayList();
        List<Clocking> sList = clockingRepository.findClockingListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromClocking(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromClocking(Clocking h) {
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
        m.put("clockingId",h.getClockingId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getClockingList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getClockingMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getClockingListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getClockingMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getClockingMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Clocking> sList = clockingRepository.findClockingListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromClocking(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/clockingDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer clockingId = dataRequest.getInteger("clockingId");  //获取student_id值
        Clocking h= null;
        Optional<Clocking> op;
        if(clockingId != null) {
            op= clockingRepository.findById(clockingId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            clockingRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getClockingInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getClockingInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer clockingId = dataRequest.getInteger("clockingId");
        Clocking s= null;
        Optional<Clocking> op;
        if(clockingId != null) {
            op= clockingRepository.findById(clockingId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromClocking(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/clockingEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse clockingEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer clockingId = dataRequest.getInteger("clockingId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Clocking h=null;
        Optional<Clocking>op;
        if(clockingId != null) {
            op= clockingRepository.findById(clockingId);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Clocking();
            h.setClockingId(getNewClockingId());
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
        clockingRepository.save(h);
        return CommonMethod.getReturnData(h.getClockingId());  //
    }

}


