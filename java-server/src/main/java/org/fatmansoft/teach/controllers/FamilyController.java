package org.fatmansoft.teach.controllers;


import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.FamilyService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.service.SystemService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.*;
import java.util.List;

/**
 * StudentController 主要是为学生管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
//类定义前面增加三行注释，说明该类可以作为支持前端请求的业务类，所有这样的类注解都可以用这样的注解。苏
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/family")

public class FamilyController {
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private SystemService systemService;

    @Autowired
    private FamilyService familyService;

    public List getFamilyMemberMapList(String numName) {
        List dataList = new ArrayList();
        List<FamilyMember> fList = familyMemberRepository.findFamilyMemberListByNumName(numName);  //数据库查询操作
        if (fList == null || fList.size() == 0)
            return dataList;
        for (int i = 0; i < fList.size(); i++) {
            FamilyMember member = fList.get(i);
            if (member != null) {
                dataList.add(familyService.getMapFromFamily(member));
            }
        }
        return dataList;
    }

    /**
     * getStudentList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @return
     */


    @PostMapping("/getFamilyMemberList")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse getFamilyMemberList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getFamilyMemberMapList(numName);
        return CommonMethod.getReturnData(dataList);

    }

    @PostMapping("/familyMemberDelete")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<FamilyMember> op;
        FamilyMember f;
        if (memberId!=null){
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f= op.get();
                familyMemberRepository.delete(f);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }



    @PostMapping("/getFamilyInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFamilyInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        FamilyMember s=null;
        Optional<FamilyMember> op;
        if (memberId != null) {
            op = familyMemberRepository.findById(memberId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(familyService.getMapFromFamily(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/familyMemberSave")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Integer studentId = dataRequest.getInteger("studentId");
        Map form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form, "num");
        String name = CommonMethod.getString(form, "name");
        String gender = CommonMethod.getString(form, "gender");
        Integer age = CommonMethod.getInteger(form, "age");
        String relation = CommonMethod.getString(form, "relation");
        String unit = CommonMethod.getString(form, "unit");


        Optional<FamilyMember> op;
        FamilyMember f = null;
        if(memberId != null) {
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f = op.get();
            }
        }
        Optional<FamilyMember> ncOp = familyMemberRepository.findFamilyMemberByNum(num);
        if (ncOp.isPresent()) {
            if (f == null || !f.getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新家庭成员编号已经存在，不能添加或修改！");
            }
        }
        if(f== null) {
            f = new FamilyMember();
//            f.setStudent(studentRepository.findById(studentId).get());
        }
        f.setNum(num);
        f.setName(name);
        f.setGender(gender);
        f.setAge(age);
        f.setRelation(relation);
        f.setUnit(unit);

        familyMemberRepository.save(f);

        return CommonMethod.getReturnData(f.getMemberId());
    }




}
