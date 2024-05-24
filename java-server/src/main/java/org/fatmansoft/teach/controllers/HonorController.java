package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
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
@RequestMapping("/api/honor")

public class HonorController {
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;
    private PersonRepository personRepository;
    public synchronized Integer getNewHonorId(){
        Integer  id = honorRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public List getHonorMapList(String numName) {
        List dataList = new ArrayList();
        List<Honor> sList = honorRepository.findHonorListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromHonor(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromHonor(Honor h) {
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
        m.put("title",h.getTitle());
        m.put("reward",h.getReward());
        m.put("time",h.getTime());
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("honorId",h.getHonorId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        return m;
    }



         @PostMapping("/getHonorList")
        public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
             String numName= dataRequest.getString("numName");
             List dataList = getHonorMapList(numName);
             return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
        }
    @PostMapping("/getHonorListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getHonorMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getHonorMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Honor> sList = honorRepository.findHonorListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromHonor(sList.get(i)));
        }
        return dataList;
    }
        @PostMapping("/honorDelete")
        @PreAuthorize(" hasRole('ADMIN')")
        public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
            Integer honorId = dataRequest.getInteger("honorId");  //获取student_id值
            Honor h= null;
            Optional<Honor> op;
            if(honorId != null) {
                op= honorRepository.findById(honorId);   //查询获得实体对象
                if(op.isPresent()) {
                    h = op.get();
                }
            }
            if(h != null) {
                honorRepository.delete(h);
            }
            return CommonMethod.getReturnMessageOK();  //通知前端操作正常
        }

        @PostMapping("/getHonorInfo")
        @PreAuthorize("hasRole('ADMIN')")
        public DataResponse getHonorInfo(@Valid @RequestBody DataRequest dataRequest) {
            Integer honorId = dataRequest.getInteger("honorId");
            Honor s= null;
            Optional<Honor> op;
            if(honorId != null) {
                op= honorRepository.findById(honorId); //根据学生主键从数据库查询学生的信息
                if(op.isPresent()) {
                    s = op.get();
                }
            }
            return CommonMethod.getReturnData(getMapFromHonor(s)); //这里回传包含学生信息的Map对象
        }
        @PostMapping("/honorEditSave")
        @PreAuthorize(" hasRole('ADMIN')")
        public DataResponse honorEditSave(@Valid @RequestBody DataRequest dataRequest) {
            Integer honorId = dataRequest.getInteger("honorId");
            Map form = dataRequest.getMap("form"); //参数获取Map对象
            String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
            Honor h=null;
            Optional<Honor>op;
            if(honorId != null) {
                op= honorRepository.findById(honorId);  //查询对应数据库中主键为id的值的实体对象
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
                h=new Honor();
                h.setHonorId(getNewHonorId());
            }
            h.setTime(CommonMethod.getString(form,"time"));
            h.setDept(CommonMethod.getString(form,"dept"));
            h.setMajor(CommonMethod.getString(form,"major"));
            h.setClassName(CommonMethod.getString(form,"className"));
            h.setCard(CommonMethod.getString(form,"card"));
            h.setGender(CommonMethod.getString(form,"gender"));
            h.setTitle(CommonMethod.getString(form,"title"));
            h.setReward(CommonMethod.getString(form,"reward"));
            h.setStudent(s);
            honorRepository.save(h);
            return CommonMethod.getReturnData(h.getHonorId());  //
        }

    }


