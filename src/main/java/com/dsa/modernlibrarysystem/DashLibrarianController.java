package com.dsa.modernlibrarysystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;


class Item
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty joinedDate;
    private final SimpleStringProperty lastLoginDate;
    private final SimpleStringProperty status;

    public Item(String id,String userName,String email,String joinedDate,String lastLoginDate,String status)
    {
        this.id = new SimpleStringProperty(id);
        this.userName = new SimpleStringProperty(userName);
        this.email = new SimpleStringProperty(email);
        this.joinedDate = new SimpleStringProperty(joinedDate);
        this.lastLoginDate = new SimpleStringProperty(lastLoginDate);
        this.status = new SimpleStringProperty(status);
    }

    public SimpleStringProperty idProperty()
    {
        return this.id;
    }

    public SimpleStringProperty userNameProperty()
    {
        return this.userName;
    }

    public SimpleStringProperty emailProperty()
    {
        return this.email;
    }
    public SimpleStringProperty joinedDateProperty()
    {
        return this.joinedDate;
    }
    public SimpleStringProperty lastLoginProperty()
    {
        return this.lastLoginDate;
    }
    public SimpleStringProperty statusProperty()
    {
        return this.status;
    }
}

public class DashLibrarianController
{
    @FXML
    protected Button deactivateBt;
    @FXML
    protected TreeTableView<Item> librarianTableView;
    @FXML
    protected TreeTableColumn<Item,String> idCol;
    @FXML
    protected TreeTableColumn<Item,String> userNameCol;
    @FXML
    protected TreeTableColumn<Item,String> emailCol;
    @FXML
    protected TreeTableColumn<Item,String> joinedDateCol;
    @FXML
    protected TreeTableColumn<Item,String> lastLoginCol;
    @FXML
    protected TreeTableColumn<Item,String> statusCol;

    @FXML
    public void initialize()
    {
        //Setting Up Row Factory
        librarianTableView.setRowFactory(tv -> new TreeTableRow<Item>()
        {
            @Override
            protected void updateItem(Item item,boolean empty)
            {
                super.updateItem(item,empty);
                getStyleClass().remove("inactive-row");
                if ( (item!= null || !empty) && item.statusProperty().get().equals("INACTIVE"))
                {
                    getStyleClass().add("inactive-row");
                }
            }
        });

        //Bind Item Select Event
        librarianTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> onItemSelected(newValue)));

        //Bind Focus Out Event
        librarianTableView.focusedProperty().addListener((observable,oldValue,newValue) -> onFocusChange(newValue));

        //Initialize TreeTableView Root
        idCol.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        userNameCol.setCellValueFactory(p -> p.getValue().getValue().userNameProperty());
        emailCol.setCellValueFactory(p -> p.getValue().getValue().emailProperty());
        joinedDateCol.setCellValueFactory(p -> p.getValue().getValue().joinedDateProperty());
        lastLoginCol.setCellValueFactory(p -> p.getValue().getValue().lastLoginProperty());
        statusCol.setCellValueFactory(p -> p.getValue().getValue().statusProperty());

        //Configuring Root
        TreeItem<Item> root = new TreeItem<>(new Item("","","","","",""));
        librarianTableView.setRoot(root);
        librarianTableView.setShowRoot(false);

        //Load Librarians
        loadLibrarianList();

    }

    protected void onFocusChange(boolean newValue)
    {
        if(!newValue)
        {
            deactivateBt.setText("Deactivate");
        }
    }

    protected void onItemSelected(TreeItem<Item> item)
    {
        if(item != null)
        {
            if (item.getValue().statusProperty().get().equals("ACTIVE"))
            {
                deactivateBt.setText("Deactivate");
            }
            else
            {
                deactivateBt.setText("Activate");
            }
        }
    }

    @FXML
    protected void addLibrarian()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("modals/LibrarianModal.fxml"));
            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.initStyle(StageStyle.TRANSPARENT);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(librarianTableView.getScene().getWindow());
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
    protected void deactivateLibrarian()
    {
        if (!librarianTableView.getSelectionModel().isEmpty())
        {
            TreeItem<Item> selItem = librarianTableView.getSelectionModel().getSelectedItem();
            if(selItem != null)
            {
                if (selItem.getValue().statusProperty().get().equals("ACTIVE") && deactivateBt.getText().equals("Deactivate"))
                {
                    Util.updateSQLQuery("UPDATE ADMINS SET STATUS = \"INACTIVE\" WHERE NAME=\"%s\"".formatted(selItem.getValue().userNameProperty().get()));
                    deactivateBt.setText("Activate");
                }
                else
                {
                    Util.updateSQLQuery("UPDATE ADMINS SET STATUS = \"ACTIVE\" WHERE NAME=\"%s\"".formatted(selItem.getValue().userNameProperty().get()));
                    deactivateBt.setText("Deactivate");
                }
                refreshList();
            }
        }
    }

    @FXML
    protected void refreshList()
    {
        librarianTableView.getRoot().getChildren().clear();
        loadLibrarianList();
    }

    protected void loadLibrarianList()
    {
        List<List<String>> result = Util.executeSQLQuery("SELECT * FROM ADMINS",new String[]{"ID","NAME","EMAIL","JOINED_DATE","STATUS"});
        if (!result.isEmpty())
        {
            for(List<String>rowData : result)
            {
                String lastLoginDate = null;
                List<List<String>> subResult = Util.executeSQLQuery("SELECT LOGIN FROM ADMIN_LOGS WHERE NAME=\"%s\" ORDER BY LOGIN DESC LIMIT 1;".formatted(rowData.get(1)),null);
                if (!subResult.isEmpty())
                {
                    lastLoginDate = subResult.get(0).get(0);
                }
                librarianTableView.getRoot().getChildren().add(new TreeItem<>(new Item(rowData.get(0),rowData.get(1),rowData.get(2),rowData.get(3),lastLoginDate,rowData.get(4))));
            }
        }
    }
}
