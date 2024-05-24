package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Technology;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.TechnologyRepository;
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
@RequestMapping("/api/technology")

public class TechnologyController {
    @Autowired
    private TechnologyRepository technologyRepository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewTechnologyId(){
        Integer  id = technologyRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getTechnologyMapList(String numName) {
        List dataList = new ArrayList();
        List<Technology> sList = technologyRepository.findTechnologyListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromTechnology(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromTechnology(Technology h) {
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
        m.put("technologyId",h.getTechnologyId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



    @PostMapping("/getTechnologyList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getTechnologyMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTechnologyListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getTechnologyMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getTechnologyMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Technology> sList = technologyRepository.findTechnologyListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromTechnology(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/technologyDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer technologyId = dataRequest.getInteger("technologyId");  //获取student_id值
        Technology h= null;
        Optional<Technology> op;
        if(technologyId != null) {
            op= technologyRepository.findById(technologyId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            technologyRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getTechnologyInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTechnologyInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer technologyId = dataRequest.getInteger("technologyId");
        Technology s= null;
        Optional<Technology> op;
        if(technologyId != null) {
            op= technologyRepository.findById(technologyId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTechnology(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/technologyEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse technologyEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer technologyId = dataRequest.getInteger("technologyId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Technology h=null;
        Optional<Technology>op;
        if(technologyId != null) {
            op= technologyRepository.findById(technologyId);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Technology();
            h.setTechnologyId(getNewTechnologyId());
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
        technologyRepository.save(h);
        return CommonMethod.getReturnData(h.getTechnologyId());  //
    }

}
