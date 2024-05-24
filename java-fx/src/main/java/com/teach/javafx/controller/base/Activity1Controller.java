package com.teach.javafx.controller.base;

import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Activity1Controller extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> timeColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map,String> typeColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map,String> placeColumn; //学生信息表 班级列

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField timeField; //学生信息  院系输入域
    @FXML
    private TextField typeField; //学生信息  专业输入域
    @FXML
    private TextField placeField; //学生信息  班级输入域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer studentId = null;  //当前编辑修改的学生的主键
    private Integer activity1Id = null;
    private ArrayList<Map> studentList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("numName","");
        res = HttpRequestUtil.request("/api/activity1/getActivity1List",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            studentList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        placeColumn.setCellValueFactory(new MapValueFactory<>("place"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeStudentInfo();
    }

    protected void changeStudentInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        studentId = CommonMethod.getInteger(form, "studentId");
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/activity1/getActivity1Info", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
    }
    @FXML
    protected void onSaveButtonClick () {
        if (numField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num", numField.getText());
        form.put("name", nameField.getText());
        form.put("time", timeField.getText());
        form.put("type", typeField.getText());
        form.put("place", placeField.getText());
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/activity1/activity1EditSave", req);
        if (res.getCode() == 0) {
            studentId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void clearPanel () {
        studentId = null;
        numField.setText("");
        nameField.setText("");
        timeField.setText("");
        typeField.setText("");
        placeField.setText("");
    }
    private void setTableViewData () {
        observableList.clear();
        for (int j = 0; j < studentList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    protected void onQueryButtonClick () {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/activity1/getActivity1List", req);
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }
    @FXML
    protected void onAddButtonClick () {
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick () {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        activity1Id = CommonMethod.getInteger(form, "activity1Id");
        DataRequest req = new DataRequest();
        req.add("activity1Id", activity1Id);
        DataResponse res = HttpRequestUtil.request("/api/activity1/activity1Delete", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onImportButtonClick () {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择学生数据表");
        fileDialog.setInitialDirectory(new File("D:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res = HttpRequestUtil.importData("/api/term/importStudentData", file.getPath(), paras);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}
