package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Log;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.repository.LogRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/log")
public class LogController {
    @Autowired
    LogRepository logRepository;
    @Autowired
    StudentRepository studentRepository;
    public Map getMapFromLog(Log l) {
        Map m = new HashMap();
        Student s;
        if(l==null){
            return m;
        }
        s=l.getStudent();
        if(s==null){
            return m;
        }
        Person p;
        p=s.getPerson();
        if(p==null){
            return m;
        }
        m.put("name",p.getName());
        m.put("num",p.getNum());
        m.put("beginTime",l.getBeginTime());
        m.put("endTime",l.getEndTime());
        m.put("place",l.getPlace());
        m.put("activity",l.getActivity());
        m.put("logId",l.getLogId());
        m.put("type",l.getType());
        return m;


    }
    public synchronized Integer getNewLogId(){
        Integer  id = logRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }


    public List getLogMapList(String typeName,String num) {
        List dataList = new ArrayList();
        if(num==null)num="";
        List<Log> sList = logRepository.findStudentListByTypeAndNameNum(typeName,num);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromLog(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/getLogList")
    public DataResponse getLogList(@Valid @RequestBody DataRequest dataRequest) {
        String typeName= dataRequest.getString("type");
        String num= dataRequest.getString("query");
        List dataList = getLogMapList(typeName,num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getLogMapListByTime(String typeName,String num,String time) {
        List dataList = new ArrayList();
        if(num==null)num="";
        List<Log> sList = logRepository.findStudentListByTypeAndNameNumAndTime(typeName,num,time);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromLog(sList.get(i)));
        }
        return dataList;
    }
    @PostMapping("/getLogListByTime")
    public DataResponse getLogListByTime(@Valid @RequestBody DataRequest dataRequest) {
        String typeName= dataRequest.getString("type");
        String num= dataRequest.getString("query");
        String time= dataRequest.getString("time");
        List dataList = getLogMapList(typeName,num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/logDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");  //获取student_id值
        Log h= null;
        Optional<Log> op;
        if(logId != null) {
            op= logRepository.findById(logId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            logRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/logEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse logEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Log h=null;
        Optional<Log> op;
        if(logId != null) {
            op= logRepository.findById(logId);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Log();
            h.setLogId(getNewLogId());
        }
        h.setBeginTime(CommonMethod.getString(form,"beginTime"));
        h.setEndTime(CommonMethod.getString(form,"endTime"));
        h.setPlace(CommonMethod.getString(form,"place"));
        h.setType(CommonMethod.getString(form,"type"));
        h.setActivity(CommonMethod.getString(form,"activity"));
        h.setStudent(s);
        logRepository.save(h);
        return CommonMethod.getReturnData(h.getLogId());  // 将studentId返回前端
    }
    @PostMapping("/getLogInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getLogInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer logId = dataRequest.getInteger("logId");
        Log s= null;
        Optional<Log> op;
        if(logId != null) {
            op= logRepository.findById(logId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromLog(s)); //这里回传包含学生信息的Map对象
    }
}
