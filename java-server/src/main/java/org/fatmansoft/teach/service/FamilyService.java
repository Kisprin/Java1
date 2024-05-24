package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class FamilyService {
    public Map getMapFromFamily(FamilyMember s) {
        Map m = new HashMap();
        m.put("memberId", s.getMemberId());
        m.put("num",s.getNum());
        m.put("name",s.getName());
        String gender = s.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("age",s.getAge());
        m.put("unit",s.getUnit());
        return m;
    }
}
