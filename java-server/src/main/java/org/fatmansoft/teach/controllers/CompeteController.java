package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Compete;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CompeteRepository;
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
@RequestMapping("/api/compete")

public class CompeteController {
    @Autowired
    private CompeteRepository competeRepository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewCompeteId(){
        Integer  id = competeRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getCompeteMapList(String numName) {
        List dataList = new ArrayList();
        List<Compete> sList = competeRepository.findCompeteListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromCompete(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromCompete(Compete h) {
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
        m.put("competeId",h.getCompeteId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getCompeteList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getCompeteMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getCompeteListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getCompeteMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getCompeteMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Compete> sList = competeRepository.findCompeteListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromCompete(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/competeDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer competeId = dataRequest.getInteger("competeId");  //获取student_id值
        Compete h= null;
        Optional<Compete> op;
        if(competeId != null) {
            op= competeRepository.findById(competeId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            competeRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getCompeteInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCompeteInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer competeId = dataRequest.getInteger("competeId");
        Compete s= null;
        Optional<Compete> op;
        if(competeId != null) {
            op= competeRepository.findById(competeId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromCompete(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/competeEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse competeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer competeId = dataRequest.getInteger("competeId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Compete h=null;
        Optional<Compete>op;
        if(competeId != null) {
            op= competeRepository.findById(competeId);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Compete();
            h.setCompeteId(getNewCompeteId());
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
        competeRepository.save(h);
        return CommonMethod.getReturnData(h.getCompeteId());  //
    }

}


