package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class FamilyController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> ageColumn; //学生信息表 年龄列
    @FXML
    private TableColumn<Map,String> genderColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 姓名列
    @FXML
    private TableColumn<Map,String> relationColumn; //学生信息表 关系列
    @FXML
    private TableColumn<Map,String> phoneColumn; //学生信息表 电话列
    @FXML
    private TableColumn<Map,String> numColumn;//学生信息表 地址列


    @FXML
    private TextField ageField; //学生信息  专业输入域
    @FXML
    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField relationField;  //学生信息  邮箱输入域
    @FXML
    private TextField phoneField;   //学生信息  电话输入域
    @FXML
    private TextField numField;  //学生信息  地址输入域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer memberId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> familylist = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < familylist.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(familylist.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("numName","");
        res = HttpRequestUtil.request("/api/family/getFamilyMemberList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            familylist = (ArrayList<Map>)res.getData();
        }

        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        ageColumn.setCellValueFactory(new MapValueFactory<>("age"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        relationColumn.setCellValueFactory(new MapValueFactory<>("relation"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("unit"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderComboBox.getItems().addAll(genderList);

    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel(){
        memberId = null;
        numField.setText("");
        nameField.setText("");
        ageField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        relationField.setText("");
        phoneField.setText("");
    }

    protected void changeFamilyInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        memberId = CommonMethod.getInteger(form,"memberId");
        DataRequest req = new DataRequest();
        req.add("memberId",memberId);
        DataResponse res = HttpRequestUtil.request("/api/family/getFamilyInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        ageField.setText(CommonMethod.getString(form, "age"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        nameField.setText(CommonMethod.getString(form, "name"));
        relationField.setText(CommonMethod.getString(form, "relation"));
        numField.setText(CommonMethod.getString(form, "num"));
        phoneField.setText(CommonMethod.getString(form, "unit"));

    }
    /**
     * 点击学生列表的某一行，根据memberId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeFamilyInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/family/getFamilyMemberList",req);
        if(res != null && res.getCode()== 0) {
            familylist = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    /**
     *  添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        System.out.println("form1"+form);
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        memberId = CommonMethod.getInteger(form,"memberId");
        DataRequest req = new DataRequest();
        req.add("memberId", memberId);
        DataResponse res = HttpRequestUtil.request("/api/family/familyMemberDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("内容为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        if(genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender",genderComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("age",ageField.getText());
        form.put("relation",relationField.getText());
        form.put("unit",phoneField.getText());
        DataRequest req = new DataRequest();
        req.add("memberId", memberId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/family/familyMemberSave",req);
        if( res.getCode() == 0) {
            memberId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
    public void doExport(){
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName",numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/family/getFamilyListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if(file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择家人数据表");
        fileDialog.setInitialDirectory(new File("D:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res =HttpRequestUtil.importData("/api/term/importStudentData",file.getPath(),paras);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}
