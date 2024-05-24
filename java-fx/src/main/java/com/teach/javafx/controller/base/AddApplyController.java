package com.teach.javafx.controller.base;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddApplyController {
    private Stage mainStage;
    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField deptField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField fatherField;
    @FXML
    private TextField fatherPhoneField;

    @FXML
    private TextField beginTimeField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField reasonField;  //学生信息  名称输入域
    @FXML
    private TextField endTimeField; //学生信息  专业输入域
    @FXML
    private TextField destinationField;   //学生信息  电话输入域
    @FXML
    private TextField zhuField;

    @FXML
    private CheckBox c1;
    @FXML
    private CheckBox c2;
    @FXML
    private CheckBox c3;
    @FXML
    private Button b1;
    @FXML
    private Button b2;
    @FXML
    private Button b3;
    String type;
    private Integer applyId;
    private Integer command;
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    public void setApplyId(Integer applyId) {
        this.applyId=applyId;
    }
    public void setCommand(Integer command){
        this.command=command;
    }
    @FXML
    protected void initialize(){
        if(applyId!=null)changeApplyInfo();
    }
    public void clearPanel(){
        applyId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");
        destinationField.setText("");
        phoneField.setText("");
        fatherField.setText("");
        fatherPhoneField.setText("");
        reasonField.setText("");
        beginTimeField.setText("");
        endTimeField.setText("");
        zhuField.setText("");
    }
    protected void changeApplyInfo() {
        if(command!=1) {
            Map form = new HashMap();
            form.put("applyId",applyId);
            if (applyId == null) {
                clearPanel();
                return;
            }
            DataRequest req = new DataRequest();
            req.put("applyId", applyId);
            DataResponse res = HttpRequestUtil.request("/api/apply/getApplyInfo", req);
            if (res.getCode() != 0) {
                MessageDialog.showDialog(res.getMsg());
                return;
            }
            form = (Map) res.getData();
            numField.setText(CommonMethod.getString(form, "num"));
            nameField.setText(CommonMethod.getString(form, "name"));
            deptField.setText(CommonMethod.getString(form, "dept"));
            majorField.setText(CommonMethod.getString(form, "major"));
            phoneField.setText(CommonMethod.getString(form, "phone"));
            fatherField.setText(CommonMethod.getString(form, "father"));
            fatherPhoneField.setText(CommonMethod.getString(form, "fatherPhone"));
            reasonField.setText(CommonMethod.getString(form, "reason"));
            destinationField.setText(CommonMethod.getString(form, "destination"));
            zhuField.setText(CommonMethod.getString(form, "zhu"));
            beginTimeField.setText(CommonMethod.getString(form, "beginTime"));
            endTimeField.setText(CommonMethod.getString(form, "endTime"));
            type=CommonMethod.getString(form, "type");
            if(type.equals("1")){
                c1();
            }else if(type.equals("2")){
                c2();
            }else if(type.equals("3")){
                c3();
            }
        }
    }
    @FXML
    protected void onSaveButtonClick() {
        if(applyId==null||command!=2) {
            if (numField.getText().equals("")) {
                mainStage.close();
                return;
            }
            Map form = new HashMap();
            form.put("num",numField.getText());
            form.put("destination",destinationField.getText());
            form.put("reason",reasonField.getText());
            form.put("beginTime",beginTimeField.getText());
            form.put("endTime",endTimeField.getText());
            form.put("zhu",zhuField.getText());
            form.put("type",type);
            DataRequest req = new DataRequest();
            if(applyId==null){
                req.put("applyId", null);
            }else {
                req.put("applyId", applyId);
            }
            req.put("form", form);
            DataResponse res = HttpRequestUtil.request("/api/apply/applyEditSave", req);
        }
        DataRequest req = new DataRequest();
        req.put("num", InformationService.getNum());
        long timemillis = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        req.put("time", df.format(new Date(timemillis)));
        req.put("change", "提交了申请");
        DataResponse res = HttpRequestUtil.request("/api/modification/add",req);
        mainStage.close();
    }
    @FXML
    protected void yesButtonClick(){
        if(applyId==null||command!=2) {
            if (numField.getText().equals("")) {
                mainStage.close();
                return;
            }
            Map form = new HashMap();
            form.put("num",numField.getText());
            form.put("destination",destinationField.getText());
            form.put("reason",reasonField.getText());
            form.put("beginTime",beginTimeField.getText());
            form.put("endTime",endTimeField.getText());
            form.put("zhu",zhuField.getText());
            form.put("situation","已通过");
            form.put("type",type);
            DataRequest req = new DataRequest();
            if(applyId==null){
                req.put("applyId", null);
            }else {
                req.put("applyId", applyId);
            }
            req.put("form", form);
            DataResponse res = HttpRequestUtil.request("/api/apply/applyEditSave", req);
        }
        DataRequest req = new DataRequest();
        req.put("num", InformationService.getNum());
        long timemillis = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        req.put("time", df.format(new Date(timemillis)));
        req.put("change", "提交了申请");
        DataResponse res = HttpRequestUtil.request("/api/modification/add",req);
        mainStage.close();
    }
    @FXML
    protected void notButtonClick(){
        if(applyId==null||command!=2) {
            if (numField.getText().equals("")) {
                mainStage.close();
                return;
            }
            Map form = new HashMap();
            form.put("num",numField.getText());
            form.put("destination",destinationField.getText());
            form.put("reason",reasonField.getText());
            form.put("beginTime",beginTimeField.getText());
            form.put("endTime",endTimeField.getText());
            form.put("zhu",zhuField.getText());
            form.put("situation","已否决");
            form.put("type",type);
            DataRequest req = new DataRequest();
            if(applyId==null){
                req.put("applyId", null);
            }else {
                req.put("applyId", applyId);
            }
            req.put("form", form);
            DataResponse res = HttpRequestUtil.request("/api/apply/applyEditSave", req);
        }
        DataRequest req = new DataRequest();
        req.put("num", InformationService.getNum());
        long timemillis = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        req.put("time", df.format(new Date(timemillis)));
        req.put("change", "提交了申请");
        DataResponse res = HttpRequestUtil.request("/api/modification/add",req);
        mainStage.close();
    }
    public void adminLog(){
        b1.setVisible(true);
        b2.setVisible(true);
        b3.setVisible(false);
    }
    @FXML
    public void c1(){
        c1.setSelected(true);
        c2.setSelected(false);
        c3.setSelected(false);
        type="1";
    }
    @FXML
    public void c2(){
        c2.setSelected(true);
        c1.setSelected(false);
        c3.setSelected(false);
        type="2";
    }
    @FXML
    public void c3(){
        c3.setSelected(true);
        c2.setSelected(false);
        c1.setSelected(false);
        type="3";
    }
    public void studentLog(){
        b3.setVisible(true);
        b2.setVisible(false);
        b1.setVisible(false);
    }
    @FXML
    protected void submitButtonClick(){
        if(applyId==null||command!=2) {
            if (numField.getText().equals("")) {
                mainStage.close();
                return;
            }
            Map form = new HashMap();
            form.put("num",numField.getText());
            form.put("destination",destinationField.getText());
            form.put("reason",reasonField.getText());
            form.put("beginTime",beginTimeField.getText());
            form.put("endTime",endTimeField.getText());
            form.put("zhu",zhuField.getText());
            form.put("situation","已提交");
            form.put("type",type);
            DataRequest req = new DataRequest();
            if(applyId==null){
                req.put("applyId", null);
            }else {
                req.put("applyId", applyId);
            }
            req.put("form", form);
            DataResponse res = HttpRequestUtil.request("/api/apply/applyEditSave", req);
        }
        DataRequest req = new DataRequest();
        req.put("num", InformationService.getNum());
        long timemillis = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        req.put("time", df.format(new Date(timemillis)));
        req.put("change", "提交了申请");
        DataResponse res = HttpRequestUtil.request("/api/modification/add",req);
        mainStage.close();
    }

}
