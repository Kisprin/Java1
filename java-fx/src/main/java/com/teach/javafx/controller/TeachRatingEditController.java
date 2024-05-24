package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageController 登录交互控制类 对应 base/message-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */

public class TeachRatingEditController {
    @FXML
    private ComboBox<OptionItem> teacherComboBox;
    private List<OptionItem> teacherList;
    @FXML
    private ComboBox<OptionItem> teachCourseComboBox;
    private List<OptionItem> teachCourseList;
    @FXML
    private TextField markField;
    private TeachRatingTableController teachRatingTableController= null;
    private Integer teachRatingId= null;
    @FXML
    public void initialize() {
    }

    @FXML
    public void okButtonClick(){
        Map data = new HashMap();
        OptionItem op;
        op = teacherComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("teacherId",Integer.parseInt(op.getValue()));
        }
        op = teachCourseComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("teachCourseId", Integer.parseInt(op.getValue()));
        }
        data.put("teachRatingId",teachRatingId);
        data.put("mark",markField.getText());
        teachRatingTableController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick(){
        teachRatingTableController.doClose("cancel",null);
    }

    public void setTeachRatingTableController(TeachRatingTableController teachRatingTableController) {
        this.teachRatingTableController = teachRatingTableController;
    }
    public void init(){
        teacherList =teachRatingTableController.getTeacherList();
        teachCourseList = teachRatingTableController.getTeachCourseList();
        teacherComboBox.getItems().addAll(teacherList );
        teachCourseComboBox.getItems().addAll(teachCourseList);
    }
    public void showDialog(Map data){
        if(data == null) {
            teachRatingId = null;
            teacherComboBox.getSelectionModel().select(-1);
            teachCourseComboBox.getSelectionModel().select(-1);
            teacherComboBox.setDisable(false);
            teachCourseComboBox.setDisable(false);
            markField.setText("");
        }else {
            teachRatingId = CommonMethod.getInteger(data,"teachRatingId");
            teacherComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(teacherList, CommonMethod.getString(data, "teacherId")));
            teachCourseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(teachCourseList, CommonMethod.getString(data, "teachCourseId")));
            teacherComboBox.setDisable(true);
            teachCourseComboBox.setDisable(true);
            markField.setText(CommonMethod.getString(data, "mark"));
        }
    }
}

