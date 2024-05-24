package com.teach.javafx.controller.base;

import com.teach.javafx.MainApplication;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import org.fatmansoft.teach.util.CommonMethod;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminApplyController {
    public void switchForm() {}
    @FXML
    private TableView<Map> applyTable;
    @FXML
    private TableColumn<Map, String> rankColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> beginTimeColumn;
    @FXML
    private TableColumn<Map, String> endTimeColumn;
    @FXML
    private TableColumn<Map, String> situationColumn;
    @FXML
    private TableColumn<Map, String> destinationColumn;



    private Integer applyId = null;

    private ArrayList<Map> applyList = new ArrayList();
    @FXML
    private TextField queryField;
    @FXML
    private Text timeField;
    private void setTimer(){
        long timemillis = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeField.setText(df.format(new Date(timemillis)));
        Timer timeAction = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long timemillis = System.currentTimeMillis();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                timeField.setText(df.format(new Date(timemillis)));
            }
        });
        timeAction.start();
    }
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    @FXML
    private Text maxNum;
    @FXML
    private Text queryNum;

    public class IDCell<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {
        @Override
        public TableCell<S, T> call(TableColumn<S, T> param) {
            TableCell cell = new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        int rowIndex = this.getIndex() + 1;
                        this.setText(String.valueOf(rowIndex));
                        if(Integer.valueOf(InformationService.getMaxApplyNum())<rowIndex){
                            InformationService.setMaxApplyNum((String.valueOf(rowIndex)));
                        }
                        maxNum.setText(InformationService.getMaxApplyNum());
                        queryNum.setText(String.valueOf(rowIndex));
                    }
                }

            };
            return cell;
        }
    }
    @FXML
    protected void modificationClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("modification.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        stage.setResizable(false);
        stage.setScene(scene);
        ModificationController popupController = fxmlLoader.getController();
        popupController.setMainStage(stage);
        stage.show();
    }

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < applyList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(applyList.get(j)));
        }
        applyTable.setItems(observableList);
    }

    @FXML
    public void initialize() {
        setTimer();
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("numName", "");
        res = HttpRequestUtil.request("/api/apply/getApplyList", req);
        if (res != null && res.getCode() == 0) {
            applyList = (ArrayList<Map>) res.getData();
        }
        rankColumn.setCellFactory(new IDCell<>());
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        beginTimeColumn.setCellValueFactory(new MapValueFactory<>("beginTime"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        endTimeColumn.setCellValueFactory(new MapValueFactory<>("endTime"));
        situationColumn.setCellValueFactory(new MapValueFactory<>("situation"));
        TableView.TableViewSelectionModel<Map> tsm = applyTable.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
    }
    @FXML
    protected void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        String s=queryField.getText();
        req.put("numName",s);
        res = HttpRequestUtil.request("/api/apply/getApplyList", req);
        if (res != null && res.getCode() == 0) {
            applyList = (ArrayList<Map>) res.getData();
        }
        rankColumn.setCellFactory(new IDCell<>());
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        beginTimeColumn.setCellValueFactory(new MapValueFactory<>("beginTime"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        endTimeColumn.setCellValueFactory(new MapValueFactory<>("endTime"));
        situationColumn.setCellValueFactory(new MapValueFactory<>("situation"));
        TableView.TableViewSelectionModel<Map> tsm = applyTable.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        queryField.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                if(event.getCode()== KeyCode.ENTER){
                    onQueryButtonClick();
                }
            }
        });
    }
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeApplyInfo();
    }
    public void clearPanel() {
        applyId = null;
    }
    protected void changeApplyInfo() {
        Map form = applyTable.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        applyId = CommonMethod.getInteger(form, "applyId");
    }

    @FXML
    protected void refresh() {
        initialize();
    }

    @FXML
    protected void deleteApply() {

        Map form = applyTable.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        applyId = CommonMethod.getInteger(form, "applyId");
        DataRequest req = new DataRequest();
        req.put("applyId", applyId);
        DataResponse res = HttpRequestUtil.request("/api/apply/applyDelete", req);
        initialize();
    }



    @FXML
    protected void applyViewButtonClick() throws IOException {
        Map form = applyTable.getSelectionModel().getSelectedItem();
        if (form == null) {
            return;
        }
        applyId = CommonMethod.getInteger(form, "applyId");
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("addApply-panel.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 714, 643);
        stage.setResizable(false);
        stage.setScene(scene);
        AddApplyController popupController = fxmlLoader.getController();
        popupController.setMainStage(stage);
        popupController.setApplyId(applyId);
        popupController.setCommand(2);
        popupController.initialize();
        stage.show();
    }

    @FXML
    protected void applyEditButtonClick() throws IOException {
        Map form = applyTable.getSelectionModel().getSelectedItem();
        if (form == null) {
            return;
        }
        applyId = CommonMethod.getInteger(form, "applyId");
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("addApply-panel.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 714, 643);
        stage.setResizable(false);
        stage.setScene(scene);
        AddApplyController popupController = fxmlLoader.getController();
        popupController.setMainStage(stage);
        popupController.setApplyId(applyId);
        popupController.setCommand(3);
        popupController.adminLog();
        popupController.initialize();
        stage.show();
    }
}
