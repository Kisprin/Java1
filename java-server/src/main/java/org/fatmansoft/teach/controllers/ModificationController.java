package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Modification;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ModificationRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/modification")
public class ModificationController {
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private PersonRepository personRepository;

    public synchronized Integer getNewModificationId(){
        Integer  id = modificationRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/getModificationList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getStudentMapList() {
        List dataList = new ArrayList();
        List<Modification> sList = modificationRepository.findModificationList("");  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromStudent(Modification s) {
        Map m = new HashMap();
        Person p=s.getPerson();
        if(p==null){
            return m;
        }
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("time", s.getTime());
        m.put("change", s.getChange());
        m.put("type", s.getType());
        return m;
    }
    @PostMapping("/add")
    public DataResponse adminAdd(@Valid @RequestBody DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        String time=dataRequest.getString("time");
        String change=dataRequest.getString("change");
        Modification m=new Modification();
        Optional<Person> op=personRepository.findByNum(num);
        Person p=null;
        if(op.isPresent()){
            p=op.get();
        }
        String user=p.getType();
        m.setPerson(p);
        if(user.equals("0")){
            m.setType("管理员");
        }else if(user.equals("1")){
            m.setType("学生");
        }else if(user.equals("2")){
            m.setType("教师");
        }
        m.setModificationId(getNewModificationId());
        m.setChange(change);
        m.setTime(time);
        modificationRepository.save(m);
        return CommonMethod.getReturnData(m.getModificationId()); //这里回传包含学生信息的Map对象
    }

}
