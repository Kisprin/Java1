package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.*;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClubService clubService;
    @Autowired
    private SystemService systemService;
    public List getClubMapList(String numName) {
        List dataList = new ArrayList();
        List<Club> cList = clubRepository.findClubListByNumName(numName);  //数据库查询操作
        if (cList == null || cList.size() == 0)
            return dataList;
        for (int i = 0; i < cList.size(); i++) {
            dataList.add(clubService.getMapFromClub(cList.get(i)));
        }
        return dataList;
    }
    public Map getMapFromClub(Club c) {
        Map m = new HashMap();
//        if(c == null)
//            return m;
        m.put("clubId", c.getClubId());
        m.put("num",c.getClubNum());
        m.put("name",c.getClubName());
        return m;
    }

    public DataResponse getClubList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getClubMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse clubDelete(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");  //获取student_id值
        Optional<Club> op;
        Club c;
        if (clubId != null) {
            op = clubRepository.findClubByClubId(clubId);   //查询获得实体对象
            if (op.isPresent()) {
                c = op.get();
                clubRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getClubInfo(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Club s = null;
        Optional<Club> op;
        if (clubId != null) {
            op = clubRepository.findById(clubId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(clubService.getMapFromClub(s)); //这里回传包含学生信息的Map对象
    }

    public DataResponse clubEditSave(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        String name = CommonMethod.getString(form, "name");
        Optional<Club> op;
        Club c = null;
        if (clubId != null) {
            op = clubRepository.findById(clubId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Optional<Club> ncOp = clubRepository.findClubByClubNum(num);
        if (ncOp.isPresent()) {
            if (c == null || !c.getClubNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新社员编号已经存在，不能添加或修改！");
            }
        }
        if (c == null)
            c = new Club();
        c.setClubNum(num);
        c.setClubName(name);
        clubRepository.save(c);  //插入新的Person记录

        return CommonMethod.getReturnData(c.getClubId());  // 将studentId返回前端
    }

}