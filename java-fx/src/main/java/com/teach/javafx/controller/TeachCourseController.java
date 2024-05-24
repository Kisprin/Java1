package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TeachCourseController 登录交互控制类 对应 teachTeachCourse-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class TeachCourseController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,Integer> creditColumn;
    @FXML
    private TableColumn<Map,String> preTeachCourseColumn;
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域
    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField creditField; //学生信息  学号输入域
    @FXML
    private TextField preTeachCourseField;  //学生信息  名称输入域

    private Integer teachTeachCourseId = null;  //当前编辑修改的学生的主键

    private List<Map> teachTeachCourseList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private void onQueryButtonClick(){
        String numName = numNameTextField.getText();
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("numName",numName);
        res = HttpRequestUtil.request("/api/teachTeachCourse/getTeachCourseList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            teachTeachCourseList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < teachTeachCourseList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(teachTeachCourseList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("numName","");
        res = HttpRequestUtil.request("/api/teachTeachCourse/getClubList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            teachTeachCourseList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory("credit"));  //设置列值工程属性
        preTeachCourseColumn.setCellValueFactory(new MapValueFactory<>("preTeachCourse"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
    }
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeClubInfo();
    }
    protected void changeClubInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        teachTeachCourseId = CommonMethod.getInteger(form,"teachTeachCourseId");
        DataRequest req = new DataRequest();
        req.add("teachTeachCourseId",teachTeachCourseId);
        DataResponse res = HttpRequestUtil.request("/api/teachTeachCourse/getClubInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        creditField.setText(CommonMethod.getString(form, "credit"));
        preTeachCourseField.setText(CommonMethod.getString(form, "preTeachCourse"));

    }
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        teachTeachCourseId = CommonMethod.getInteger(form,"teachTeachCourseId");
        DataRequest req = new DataRequest();
        req.add("teachTeachCourseId", teachTeachCourseId);
        DataResponse res = HttpRequestUtil.request("/api/teachTeachCourse/teachTeachCourseDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    protected void onSaveButtonClick() {
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("编号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("credit",creditField.getText());
        form.put("preTeachCourse",preTeachCourseField.getText());
        DataRequest req = new DataRequest();
        req.add("teachTeachCourseId", teachTeachCourseId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/teachTeachCourse/teachTeachCourseSave",req);
        if(res.getCode() == 0) {
            teachTeachCourseId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    public void clearPanel(){
        teachTeachCourseId = null;
        numField.setText("");
        nameField.setText("");
        creditField.setText("");
        preTeachCourseField.setText("");
    }
}
