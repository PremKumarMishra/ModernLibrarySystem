package com.dsa.modernlibrarysystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MemberItem
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty regd;
    private final SimpleStringProperty joinedDate;
    private final SimpleStringProperty status;

    public MemberItem(String id,String name,String email,String regd,String joinedDate,String status)
    {
        this.id = new SimpleStringProperty(id);
        this.userName = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.regd = new SimpleStringProperty(regd);
        this.joinedDate = new SimpleStringProperty(joinedDate);
        this.status = new SimpleStringProperty(status);

    }

    public SimpleStringProperty idProperty() {return this.id;}
    public SimpleStringProperty userNameProperty() {return this.userName;}
    public SimpleStringProperty emailProperty() {return this.email;}
    public SimpleStringProperty regdProperty() {return this.regd;}
    public SimpleStringProperty joinedDateProperty() {return this.joinedDate;}
    public SimpleStringProperty statusProperty() {return this.status;}
}

public class DashMemberController
{
    @FXML
    protected TreeTableView<MemberItem> memberTableView;
    @FXML
    protected TreeTableColumn<MemberItem,String> idCol;
    @FXML
    protected TreeTableColumn<MemberItem,String> nameCol;
    @FXML
    protected TreeTableColumn<MemberItem,String> emailCol;
    @FXML
    protected TreeTableColumn<MemberItem,String> regdCol;
    @FXML
    protected TreeTableColumn<MemberItem,String> joinedDateCol;
    @FXML
    protected TreeTableColumn<MemberItem,String> statusCol;

    @FXML
    protected TextField searchField;

    @FXML
    public void initialize()
    {
        //Setup Search Listener
        searchField.textProperty().addListener((observable,oldVa,newVal) -> onSearch(newVal));

        //INIT Cell Factory
        idCol.setCellValueFactory(p->p.getValue().getValue().idProperty());
        nameCol.setCellValueFactory(p->p.getValue().getValue().userNameProperty());
        emailCol.setCellValueFactory(p->p.getValue().getValue().emailProperty());
        regdCol.setCellValueFactory(p->p.getValue().getValue().regdProperty());
        joinedDateCol.setCellValueFactory(p->p.getValue().getValue().joinedDateProperty());
        statusCol.setCellValueFactory(p->p.getValue().getValue().statusProperty());

        //Set TreeTableView Root
        TreeItem<MemberItem> root = new TreeItem<>(new MemberItem("","","","","",""));
        root.setExpanded(false);
        memberTableView.setRoot(root);
        memberTableView.setShowRoot(false);

        //Load Table Data
        loadMembersList();
    }

    @FXML
    protected void addMember()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("modals/MembersModal.fxml"));
            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.initStyle(StageStyle.TRANSPARENT);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(memberTableView.getScene().getWindow());
            modal.getScene().setFill(Color.TRANSPARENT);
            modal.showAndWait();
            refreshList();
        }
        catch (IOException e)
        {
            AlertDialog.show("FXML Error","Failed to load FXML file\nError : " + e.getMessage(),AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void editMember()
    {
        try
        {
            if (!memberTableView.getSelectionModel().isEmpty())
            {
                TreeItem<MemberItem> item = memberTableView.getSelectionModel().getSelectedItem();
                if(item != null)
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("modals/MembersEditModal.fxml"));
                    Parent root = loader.load();
                    MemberEditModalController controller = loader.getController();
                    controller.initValues(item.getValue().userNameProperty().get(),item.getValue().emailProperty().get(),item.getValue().regdProperty().get(),Integer.parseInt(item.getValue().idProperty().get()));
                    Stage modal = new Stage();
                    modal.setScene(new Scene(root));
                    modal.initStyle(StageStyle.TRANSPARENT);
                    modal.initModality(Modality.APPLICATION_MODAL);
                    modal.initOwner(memberTableView.getScene().getWindow());
                    modal.getScene().setFill(Color.TRANSPARENT);
                    modal.showAndWait();
                    refreshList();
                }

            }

        }
        catch (IOException e)
        {
            AlertDialog.show("FXML Error","Failed to load FXML file\nError : " + e.getMessage(),AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void deleteMember()
    {
        if (!memberTableView.getSelectionModel().isEmpty())
        {
            TreeItem<MemberItem> item = memberTableView.getSelectionModel().getSelectedItem();
            if (item != null)
            {
                boolean res = AlertDialog.show("Delete Confirmation","Are you sure you want to remove this member ?",AlertDialog.AlertType.CONFIRMATION);
                if(res)
                {
                    int memberID = Integer.parseInt(item.getValue().idProperty().get());
                    Util.updateSQLQuery("DELETE FROM MEMBERS WHERE ID = %d;".formatted(memberID));
                    Util.updateSQLQuery("DELETE FROM LOGS WHERE MEMBER_ID = %d;".formatted(memberID));
                    memberTableView.getRoot().getChildren().remove(item);
                }
            }
        }
    }

    @FXML
    protected void onSearch(String newVal)
    {
        if (!newVal.isBlank())
        {
            List<TreeItem<MemberItem>> searchRes = new ArrayList<>();
            memberTableView.getRoot().getChildren().forEach(child -> {
                if(child.getValue().regdProperty().get().toLowerCase().startsWith(newVal.toLowerCase()) || child.getValue().userNameProperty().get().toLowerCase().startsWith(newVal.toLowerCase()) || child.getValue().emailProperty().get().toLowerCase().startsWith(newVal.toLowerCase()))
                {
                    searchRes.add(child);
                }
            });
            //Show Search Results
            memberTableView.getRoot().getChildren().clear();
            for(TreeItem<MemberItem> child : searchRes)
            {
                memberTableView.getRoot().getChildren().add(child);
            }
        }
        else
        {
            refreshList();
        }
    }

    @FXML
    protected void refreshList()
    {
        memberTableView.getRoot().getChildren().clear();
        loadMembersList();
    }

    @FXML
    protected void loadMembersList()
    {
        List<List<String>> result = Util.executeSQLQuery("SELECT ID,NAME,EMAIL,REGD,JOINED_DATE,STATUS FROM MEMBERS",new String[]{"ID","NAME","EMAIL","REGD","JOINED_DATE","STATUS"});
        if (!result.isEmpty())
        {
            for(List<String>rowData : result)
            {
                TreeItem<MemberItem> item = new TreeItem<>(new MemberItem(rowData.get(0),rowData.get(1),rowData.get(2),rowData.get(3),rowData.get(4),rowData.get(5)));
                memberTableView.getRoot().getChildren().add(item);
            }
        }
    }
}
