package com.teach.javafx.controller.base;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModificationController {
    private Stage mainStage;
    @FXML
    private TableView<Map> modificationTable;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> rankColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> typeColumn;
    @FXML
    private TableColumn<Map,String> timeColumn;
    @FXML
    private TableColumn<Map,String> changeColumn;
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private ArrayList<Map> studentList = new ArrayList();

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
                    }
                }

            };
            return cell;
        }
    }
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentList.get(j)));
        }
        modificationTable.setItems(observableList);
        modificationTable.getSortOrder().add(timeColumn);
        timeColumn.setSortType(TableColumn.SortType.DESCENDING);
        modificationTable.sort();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    protected void initialize(){

        DataResponse res;
        DataRequest req =new DataRequest();
        res = HttpRequestUtil.request("/api/modification/getModificationList",req);
        if(res != null && res.getCode()== 0) {
            studentList = (ArrayList<Map>)res.getData();
        }
        rankColumn.setCellFactory(new IDCell<>());
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        changeColumn.setCellValueFactory(new MapValueFactory<>("change"));
        setTableViewData();
    }
    @FXML
    protected void onCancelButtonClick(){
        mainStage.close();
    }
}
