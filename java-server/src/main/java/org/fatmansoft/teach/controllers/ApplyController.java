
package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;
/**
 *  StudentController 主要是为学生管理数据管理提供的Web请求服务
 *
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/apply")

public class ApplyController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用
    @Autowired
    private ApplyRepository applyRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private LogRepository logRepository;
    public synchronized Integer getNewLogId(){
        Integer  id = logRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    public synchronized Integer getNewApplyId(){
        Integer  id = applyRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    public List getApplyMapList(String numName) {
        List dataList = new ArrayList();
        List<Apply> sList = applyRepository.findApplyListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromApply(sList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromApply(Apply h) {
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
        m.put("beginTime",h.getBeginTime());
        m.put("endTime",h.getEndTime());
        m.put("type",h.getType());
        m.put("zhu",h.getZhu());
        m.put("destination",h.getDestination());
        m.put("situation",h.getSituation());
        m.put("reason",h.getReason());
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("applyId",h.getApplyId());
        m.put("name",p.getName());
        m.put("num",p.getNum());
        m.put("dept",p.getDept());
        m.put("major",s.getMajor());
        m.put("phone",p.getPhone());
        m.put("father",s.getFather());
        m.put("fatherPhone",s.getFatherPhone());
        return m;
    }

    @PostMapping("/getApplyList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getApplyMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getApplyListByQuery")
    public DataResponse getStudentListByQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        String query= dataRequest.getString("query");
        List dataList = getApplyMapListByQuery(numName,query);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getApplyMapListByQuery(String numName,String query) {
        List dataList = new ArrayList();
        List<Apply> sList = applyRepository.findApplyListByNumNameAndQuery(numName,query);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromApply(sList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/applyDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer applyId = dataRequest.getInteger("applyId");  //获取student_id值
        Apply h= null;
        Optional<Apply> op;
        if(applyId != null) {
            op= applyRepository.findById(applyId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h != null) {
            applyRepository.delete(h);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/applyEditSave")
    public DataResponse applyEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer applyId = dataRequest.getInteger("applyId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Apply h=null;
        Optional<Apply>op;
        if(applyId != null) {
            op= applyRepository.findById(applyId);  //查询对应数据库中主键为id的值的实体对象
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
            h=new Apply();
            h.setApplyId(getNewApplyId());
            h.setSituation("已提交");
        }
        h.setBeginTime(CommonMethod.getString(form,"beginTime"));
        h.setEndTime(CommonMethod.getString(form,"endTime"));
        h.setZhu(CommonMethod.getString(form,"zhu"));
        h.setDestination(CommonMethod.getString(form,"destination"));
        h.setType(CommonMethod.getString(form,"type"));
        h.setReason(CommonMethod.getString(form,"reason"));
        h.setSituation(CommonMethod.getString(form,"situation"));
        if(h.getSituation().equals("已通过")){
            Log l=new Log();
            l.setLogId(getNewLogId());
            l.setBeginTime(CommonMethod.getString(form,"beginTime"));
            l.setEndTime(CommonMethod.getString(form,"endTime"));
            l.setPlace(CommonMethod.getString(form,"destination"));
            l.setType("1");
            l.setActivity(CommonMethod.getString(form,"reason"));
            l.setStudent(s);
            logRepository.save(l);
        }
        h.setStudent(s);
        applyRepository.save(h);
        return CommonMethod.getReturnData(h.getApplyId());  // 将studentId返回前端
    }
    @PostMapping("/getApplyInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getApplyInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer applyId = dataRequest.getInteger("applyId");
        Apply s= null;
        Optional<Apply> op;
        if(applyId != null) {
            op= applyRepository.findById(applyId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromApply(s)); //这里回传包含学生信息的Map对象
    }
}
