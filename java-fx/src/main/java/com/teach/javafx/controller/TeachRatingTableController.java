package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeachRatingTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> teacherNumColumn;
    @FXML
    private TableColumn<Map,String> teacherNameColumn;
    @FXML
    private TableColumn<Map,String> degreeColumn;
    @FXML
    private TableColumn<Map,String> teachCourseNumColumn;
    @FXML
    private TableColumn<Map,String> teachCourseNameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> markColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;


    private ArrayList<Map> teachRatingList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> teacherComboBox;


    private List<OptionItem> teacherList;
    @FXML
    private ComboBox<OptionItem> teachCourseComboBox;


    private List<OptionItem> teachCourseList;

    private TeachRatingEditController teachRatingEditController = null;
    private Stage stage = null;
    public List<OptionItem> getTeacherList() {
        return teacherList;
    }
    public List<OptionItem> getTeachCourseList() {
        return teachCourseList;
    }


    @FXML
    private void onQueryButtonClick(){
        Integer teacherId = 0;
        Integer teachCourseId = 0;
        OptionItem op;
        op = teacherComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            teacherId = Integer.parseInt(op.getValue());
        op = teachCourseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            teachCourseId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("teacherId",teacherId);
        req.add("teachCourseId",teachCourseId);
        res = HttpRequestUtil.request("/api/teachRating/getTeachRatingList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            teachRatingList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < teachRatingList.size(); j++) {
            map = teachRatingList.get(j);
            editButton = new Button("编辑");
            editButton.setId("edit"+j);
            editButton.setOnAction(e->{
                editItem(((Button)e.getSource()).getId());
            });
            map.put("edit",editButton);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }
    public void editItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = teachRatingList.get(j);
        initDialog();
        teachRatingEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();

    }
    @FXML
    public void initialize() {


        teacherNumColumn.setCellValueFactory(new MapValueFactory("teacherNum"));  //设置列值工程属性
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        degreeColumn.setCellValueFactory(new MapValueFactory<>("degree"));
        teachCourseNumColumn.setCellValueFactory(new MapValueFactory<>("teachCourseNum"));
        teachCourseNameColumn.setCellValueFactory(new MapValueFactory<>("teachCourseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/teachRating/getTeacherItemOptionList",req); //从后台获取所有学生信息列表集合
        teachCourseList = HttpRequestUtil.requestOptionItemList("/api/teachRating/getTeachCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        teacherComboBox.getItems().addAll(item);
        teacherComboBox.getItems().addAll(teacherList);
        teachCourseComboBox.getItems().addAll(item);
        teachCourseComboBox.getItems().addAll(teachCourseList);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("teachRating-edit-dialog-panel.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩录入对话框！");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });
            teachRatingEditController = (TeachRatingEditController) fxmlLoader.getController();
            teachRatingEditController.setTeachRatingTableController(this);
            teachRatingEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doClose(String cmd, Map data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer teacherId = CommonMethod.getInteger(data,"teacherId");
        if(teacherId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer teachCourseId = CommonMethod.getInteger(data,"teachCourseId");
        if(teachCourseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }
        DataRequest req =new DataRequest();
        req.add("teacherId",teacherId);
        req.add("teachCourseId",teachCourseId);
        req.add("teachRatingId",CommonMethod.getInteger(data,"teachRatingId"));
        req.add("mark",CommonMethod.getInteger(data,"mark"));
        res = HttpRequestUtil.request("/api/teachRating/teachRatingSave",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        teachRatingEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        teachRatingEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer teachRatingId = CommonMethod.getInteger(form,"teachRatingId");
        DataRequest req = new DataRequest();
        req.add("teachRatingId", teachRatingId);
        DataResponse res = HttpRequestUtil.request("/api/teachRating/teachRatingDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}