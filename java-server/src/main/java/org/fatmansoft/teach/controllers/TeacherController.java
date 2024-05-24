package org.fatmansoft.teach.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.TeacherService;
import org.fatmansoft.teach.service.SystemService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

/**
 * TeacherController 主要是为学生管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeacherController 中要使用TeacherRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， teacherRepository 相当于TeacherRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // TeacherController中的方法可以直接使用
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private TeacherRepository teacherRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private TeachRatingRepository teachRatingRepository;  //成绩数据操作自动注入
    @Autowired
    private SalaryRepository salaryRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private TeacherFamilyMemberRepository familyMemberRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private SystemService systemService;

    /**
     * getMapFromTeacher 将学生表属性数据转换复制MAp集合里
     * @param
     * @return
     */

    /**
     * getTeacherMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     *
     * @param numName 输入参数
     * @return Map List 集合
     */
    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(teacherService.getMapFromTeacher(sList.get(i)));
        }
        return dataList;
    }

    /**
     * getTeacherList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @return
     */


    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    /**
     * teacherDelete 删除学生信息Web服务 Teacher页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Teacher关联Person 所以要先删除Teacher,User，再删除Person
     *
     * @param dataRequest 前端teacherId 药删除的学生的主键 teacher_id
     * @return 正常操作
     */

    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");  //获取teacher_id值
//        teacherId = null;
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if (uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }
            Person p = s.getPerson();
            teacherRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getTeacherInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 teacherId 查询学生信息的主键 teacher_id
     * @return 根据teacherId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(teacherService.getMapFromTeacher(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * teacherEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Teacher 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * teacherId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     *
     * @return 新建修改学生的主键 teacher_id 返回前端
     */
    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Teacher s = null;
        Person p;
        User u;
        Optional<Teacher> op;
        Integer personId;
        boolean isNew = false;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("1");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            String password = encoder.encode("123456");
            u = new User();
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getUserId());
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Teacher();   // 创建实体对象
            s.setPerson(p);
            teacherRepository.saveAndFlush(s);  //插入新的Teacher记录
            isNew = true;
        } else {
            p = s.getPerson();
            isNew = false;
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setTitle(CommonMethod.getString(form, "title"));
        s.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(s);  //修改保存学生信息
        systemService.modifyLog(s,isNew);
        return CommonMethod.getReturnData(s.getTeacherId());  // 将teacherId返回前端
    }


    /**
     * getTeacherTeachRatingList 将TeachRating对象列表集合转换成TeachRating Map对象列表集合
     *
     * @param sList
     * @return
     */
    public List getTeacherTeachRatingList(List<TeachRating> sList) {
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        TeachCourse c;
        for (TeachRating s : sList) {
            m = new HashMap();
            c = s.getTeachCourse();
            m.put("teacherNum", s.getTeacher().getPerson().getNum());
            m.put("teachRatingId", s.getTeachRatingId());
            m.put("teachCourseNum", c.getNum());
            m.put("teachCourseName", c.getName());
            m.put("credit", c.getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            list.add(m);
        }
        return list;
    }

    /**
     * getTeacherMarkList 计算学生的的成绩等级
     *
     * @param sList 学生成绩列表
     * @return 成绩等级Map对象列表
     */
    public List getTeacherMarkList(List<TeachRating> sList) {
        String title[] = {"优", "良", "中", "及格", "不及格"};
        int count[] = new int[5];
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        TeachCourse c;
        for (TeachRating s : sList) {
            c = s.getTeachCourse();
            if (s.getMark() >= 90)
                count[0]++;
            else if (s.getMark() >= 80)
                count[1]++;
            else if (s.getMark() >= 70)
                count[2]++;
            else if (s.getMark() >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for (int i = 0; i < 5; i++) {
            m = new HashMap();
            m.put("name", title[i]);
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }

    /**
     * getTeacherSalaryList 获取学生的消费Map对象列表集合
     *
     * @param teacherId
     * @return
     */
    public List getTeacherSalaryList(Integer teacherId) {
        List<Salary> sList = salaryRepository.findListByTeacher(teacherId);  // 查询某个学生消费记录集合
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        TeachCourse c;
        for (Salary s : sList) {
            m = new HashMap();
            m.put("title", s.getDay());
            m.put("value", s.getMoney());
            list.add(m);
        }
        return list;
    }

    /**
     * getTeacherIntroduceData 前端获取学生个人简历数据请求服务
     *
     * @param dataRequest 从前端获取 teacherId 查询学生信息的主键 teacher_id
     * @return 根据teacherId从数据库中查出相关数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getTeacherIntroduceData")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getTeacherIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        String username = CommonMethod.getUsername();
        Optional<Teacher> sOp = teacherRepository.findByPersonNum(username);  // 查询获得 Teacher对象
        if (!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Teacher s = sOp.get();
        Map info = teacherService.getMapFromTeacher(s);  // 查询学生信息Map对象
        List<TeachRating> sList = teachRatingRepository.findByTeacherTeacherId(s.getTeacherId()); //获得学生成绩对象集合
        Map data = new HashMap();
        data.put("info", info);
        data.put("teachRatingList", getTeacherTeachRatingList(sList));
        data.put("markList", getTeacherMarkList(sList));
        data.put("salaryList", getTeacherSalaryList(s.getTeacherId()));
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }

    /**
     * saveTeacherIntroduce 前端学生个人简介信息introduce提交服务
     *
     * @param dataRequest 从前端获取 teacherId teacher表 teacher_id introduce 学生个人简介信息
     * @return 操作正常
     */

    @PostMapping("/saveTeacherIntroduce")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse saveTeacherIntroduce(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        String introduce = dataRequest.getString("introduce");
        Optional<Teacher> sOp = teacherRepository.findById(teacherId);
        if (!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Teacher s = sOp.get();
        Person p = s.getPerson();
        p.setIntroduce(introduce);
        personRepository.save(p);
        return CommonMethod.getReturnMessageOK();
    }


    public String importSalaryData(Integer teacherId,InputStream in){
        try {
            Teacher teacher = teacherRepository.findById(teacherId).get();
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            int i;
            i = 1;
            String day, money;
            Optional<Salary> fOp;
            Double dMoney;
            Salary f;
            rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                if (cell == null)
                    break;
                day = cell.getStringCellValue();  //获取一行消费记录 日期 金额
                cell = row.getCell(1);
                money = cell.getStringCellValue();
                fOp = salaryRepository.findByTeacherTeacherIdAndDay(teacherId, day);  //查询是否存在记录
                if (!fOp.isPresent()) {
                    f = new Salary();
                    f.setDay(day);
                    f.setTeacher(teacher);  //不存在 添加
                } else {
                    f = fOp.get();  //存在 更新
                }
                if (money != null && money.length() > 0)
                    dMoney = Double.parseDouble(money);
                else
                    dMoney = 0d;
                f.setMoney(dMoney);
                salaryRepository.save(f);
            }
            workbook.close();  //关闭Excl输入流
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传错误！";
        }

    }
    /**
     * importSalaryData 前端上传消费流水Excl表数据服务
     *
     * @param barr         文件二进制数据
     * @param uploader     上传者
     * @param teacherIdStr teacher 主键
     * @param fileName     前端上传的文件名
     * @return
     */
    @PostMapping(path = "/importSalaryData")
    public DataResponse importSalaryData(@RequestBody byte[] barr,
                                      @RequestParam(name = "uploader") String uploader,
                                      @RequestParam(name = "teacherId") String teacherIdStr,
                                      @RequestParam(name = "fileName") String fileName) {
        Integer teacherId =  Integer.parseInt(teacherIdStr);
        String msg = importSalaryData(teacherId,new ByteArrayInputStream(barr));
        if(msg == null)
            return CommonMethod.getReturnMessageOK();
        else
            return CommonMethod.getReturnMessageError(msg);
    }

    /**
     * getTeacherListExcl 前端下载导出学生基本信息Excl表数据
     *
     * @param dataRequest
     * @return
     */
    @PostMapping("/getTeacherListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List list = getTeacherMapList(numName);
        Integer widths[] = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String titles[] = {"序号", "学号", "姓名", "学院", "专业", "班级", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "teacher.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell cell[] = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map m;
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = (Map) list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "title"));
                cell[5].setCellValue(CommonMethod.getString(m, "degree"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
            }
        }
        try {
            StreamingResponseBody stream = outputStream -> {
                wb.write(outputStream);
            };
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    /**
     * getTeacherIntroducePdf 生成获取个人简历的PDF数据流服务
     *
     * @param dataRequest teacherId 学生主键
     * @return 返回PDF文件二进制数据
     */
    @PostMapping("/getTeacherIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getTeacherIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = teacherRepository.getById(teacherId);  //查询获得Teacher对象
        Map info = teacherService.getMapFromTeacher(s); //获得学生信息
        String content = (String) info.get("introduce");  // 个人简历的HTML字符串
        content = CommonMethod.addHeadInfo(content, "<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");  // 插入由HTML转换PDF需要的头信息
        System.out.println(content);
        content = CommonMethod.removeErrorString(content, "&nbsp;", "style=\"font-family: &quot;&quot;;\""); //删除无法转化不合法的HTML标签
        content = CommonMethod.replaceNameValue(content, info); //将HTML中标记串${name}等替换成学生实际的信息
        return baseService.getPdfDataFromHtml(content); //生成学生简历PDF二进制数据
    }

    @PostMapping("/getTeacherPageData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getTeacherPageData(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List dataList = new ArrayList();
        Page<Teacher> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = teacherRepository.findTeacherPageByNumName(numName, pageable);
        Map m;
        Teacher s;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List list = page.getContent();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    s = (Teacher) list.get(i);
                    m = teacherService.getMapFromTeacher(s);
                    dataList.add(m);
                }
            }
        }
        HashMap data = new HashMap();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }


    public byte[] getTeacherIntroduceItextPdfData(Integer teacherId) {
        byte data[] = null;
        try {
            Map<String, Object> map = new HashMap<>();
            //设置纸张规格为A4纸
            Rectangle rect = new Rectangle(PageSize.A4);
            //创建文档实例
            Document doc = new Document(rect);
            //添加中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            //设置字体样式
            Font textFont = new Font(bfChinese, 11, Font.NORMAL); //正常
            //Font redTextFont = new Font(bfChinese,11,Font.NORMAL,Color.RED); //正常,红色
            Font boldFont = new Font(bfChinese, 11, Font.BOLD); //加粗
            //Font redBoldFont = new Font(bfChinese,11,Font.BOLD,Color.RED); //加粗,红色
            Font firsetTitleFont = new Font(bfChinese, 22, Font.BOLD); //一级标题
            Font secondTitleFont = new Font(bfChinese, 15, Font.BOLD, CMYKColor.BLUE); //二级标题
            Font underlineFont = new Font(bfChinese, 11, Font.UNDERLINE); //下划线斜体
            //设置字体
            com.itextpdf.text.Font FontChinese24 = new com.itextpdf.text.Font(bfChinese, 24, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese16 = new com.itextpdf.text.Font(bfChinese, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //设置要导出的pdf的标题
            String title = "霸道流氓气质";
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.newPage();
            //新建段落
            //使用二级标题 颜色为蓝色
            Paragraph p1 = new Paragraph("二级标题", secondTitleFont);
            //设置行高
            p1.setLeading(0);
            //设置标题居中
            p1.setAlignment(Element.ALIGN_CENTER);
            //将段落添加到文档上
            doc.add(p1);
            //设置一个空的段落，行高为18  什么内容都不显示
            Paragraph blankRow1 = new Paragraph(18f, " ", FontChinese11);
            doc.add(blankRow1);
            //新建表格 列数为2
            PdfPTable table1 = new PdfPTable(2);
            //给表格设置宽度
            int width1[] = {80, 60};
            table1.setWidths(width1);
            //新建单元格
            String name = "霸道";
            String gender = "男";
            //给单元格赋值 每个单元格为一个段落，每个段落的字体为加粗
            PdfPCell cell11 = new PdfPCell(new Paragraph("姓名：  " + name, boldFont));
            PdfPCell cell12 = new PdfPCell(new Paragraph("性别：  " + gender, boldFont));
            //设置单元格边框为0
            cell11.setBorder(0);
            cell12.setBorder(0);
            table1.addCell(cell11);
            table1.addCell(cell12);
            doc.add(table1);
            PdfPTable table3 = new PdfPTable(2);
            table3.setWidths(width1);
            PdfPCell cell15 = new PdfPCell(new Paragraph("博客主页： https://me.csdn.net/BADAO_LIUMANG_QIZHI  ", boldFont));
            PdfPCell cell16 = new PdfPCell(new Paragraph("当前时间：  " + DateTimeTool.parseDateTime(new Date(), "yyy_MM_dd"), boldFont));
            cell15.setBorder(0);
            cell16.setBorder(0);
            table3.addCell(cell15);
            table3.addCell(cell16);
            doc.add(table3);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/getTeacherIntroduceItextPdf")
    public ResponseEntity<StreamingResponseBody> getTeacherIntroduceItextPdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        return CommonMethod.getByteDataResponseBodyPdf(getTeacherIntroduceItextPdfData(teacherId));
    }

    public void exportPdfServlet(Long orderId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置响应格式等
        response.setContentType("application/pdf");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        Map<String, Object> map = new HashMap<>();
        //设置纸张规格为A4纸
        Rectangle rect = new Rectangle(PageSize.A4);
        //创建文档实例
        Document doc = new Document(rect);
        //添加中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //设置字体样式
        Font textFont = new Font(bfChinese, 11, Font.NORMAL); //正常
        //Font redTextFont = new Font(bfChinese,11,Font.NORMAL,Color.RED); //正常,红色
        Font boldFont = new Font(bfChinese, 11, Font.BOLD); //加粗
        //Font redBoldFont = new Font(bfChinese,11,Font.BOLD,Color.RED); //加粗,红色
        Font firsetTitleFont = new Font(bfChinese, 22, Font.BOLD); //一级标题
        Font secondTitleFont = new Font(bfChinese, 15, Font.BOLD, CMYKColor.BLUE); //二级标题
        Font underlineFont = new Font(bfChinese, 11, Font.UNDERLINE); //下划线斜体
        //设置字体
        com.itextpdf.text.Font FontChinese24 = new com.itextpdf.text.Font(bfChinese, 24, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese16 = new com.itextpdf.text.Font(bfChinese, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.ITALIC);
        com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);

        //设置要导出的pdf的标题
        String title = "霸道流氓气质";
        response.setHeader("Content-disposition", "attachment; filename=".concat(String.valueOf(URLEncoder.encode(title + ".pdf", "UTF-8"))));
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();
        doc.newPage();
        //新建段落
        //使用二级标题 颜色为蓝色
        Paragraph p1 = new Paragraph("二级标题", secondTitleFont);
        //设置行高
        p1.setLeading(0);
        //设置标题居中
        p1.setAlignment(Element.ALIGN_CENTER);
        //将段落添加到文档上
        doc.add(p1);
        //设置一个空的段落，行高为18  什么内容都不显示
        Paragraph blankRow1 = new Paragraph(18f, " ", FontChinese11);
        doc.add(blankRow1);
        //新建表格 列数为2
        PdfPTable table1 = new PdfPTable(2);
        //给表格设置宽度
        int width1[] = {80, 60};
        table1.setWidths(width1);
        //新建单元格
        String name = "霸道";
        String gender = "男";
        //给单元格赋值 每个单元格为一个段落，每个段落的字体为加粗
        PdfPCell cell11 = new PdfPCell(new Paragraph("姓名：  " + name, boldFont));
        PdfPCell cell12 = new PdfPCell(new Paragraph("性别：  " + gender, boldFont));
        //设置单元格边框为0
        cell11.setBorder(0);
        cell12.setBorder(0);
        table1.addCell(cell11);
        table1.addCell(cell12);
        doc.add(table1);
        PdfPTable table3 = new PdfPTable(2);
        table3.setWidths(width1);
        PdfPCell cell15 = new PdfPCell(new Paragraph("博客主页： https://me.csdn.net/BADAO_LIUMANG_QIZHI  ", boldFont));
        PdfPCell cell16 = new PdfPCell(new Paragraph("当前时间：  " + DateTimeTool.parseDateTime(new Date(), "yyy_MM_dd"), boldFont));
        cell15.setBorder(0);
        cell16.setBorder(0);
        table3.addCell(cell15);
        table3.addCell(cell16);
        doc.add(table3);
        doc.close();
    }

    /*
        TeacherFamilyMember
     */
    @PostMapping("/getTeacherFamilyMemberList")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse getTeacherFamilyMemberList(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        List<TeacherFamilyMember> fList = familyMemberRepository.findByTeacherTeacherId(teacherId);
        List dataList = new ArrayList();
        Map m;
        if (fList != null) {
            for (TeacherFamilyMember f : fList) {
                m = new HashMap();
                m.put("memberId", f.getMemberId());
                m.put("teacherId", f.getTeacher().getTeacherId());
                m.put("relation", f.getRelation());
                m.put("name", f.getName());
                m.put("gender", f.getGender());
                m.put("age", f.getAge());
                m.put("unit", f.getUnit());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/familyMemberSave")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberSave(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        Integer teacherId = CommonMethod.getInteger(form,"teacherId");
        Integer memberId = CommonMethod.getInteger(form,"memberId");
        Optional<TeacherFamilyMember> op;
        TeacherFamilyMember f = null;
        if(memberId != null) {
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f== null) {
            f = new TeacherFamilyMember();
            f.setTeacher(teacherRepository.findById(teacherId).get());
        }
        f.setRelation(CommonMethod.getString(form,"relation"));
        f.setName(CommonMethod.getString(form,"name"));
        f.setGender(CommonMethod.getString(form,"gender"));
        f.setAge(CommonMethod.getInteger(form,"age"));
        f.setUnit(CommonMethod.getString(form,"unit"));
        familyMemberRepository.save(f);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/familyMemberDelete")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<TeacherFamilyMember> op;
        op = familyMemberRepository.findById(memberId);
        if(op.isPresent()) {
            familyMemberRepository.delete(op.get());
        }
        return CommonMethod.getReturnMessageOK();
    }


    @PostMapping("/importSalaryDataWeb")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse importSalaryDataWeb(@RequestParam Map request, @RequestParam("file") MultipartFile file) {
        Integer teacherId = CommonMethod.getInteger(request, "teacherId");
        try {
            String msg= importSalaryData(teacherId,file.getInputStream());
            if(msg == null)
                return CommonMethod.getReturnMessageOK();
            else
                return CommonMethod.getReturnMessageError(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageError("上传错误！");
    }

}
