package com.dsa.modernlibrarysystem;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.stream.Collectors;


class ActivityItem
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty member;
    private final SimpleStringProperty issued_by;
    private final SimpleStringProperty issued_date;
    private final SimpleStringProperty status;

    public ActivityItem(String id,String title,String member,String issued_by,String issued_date,String status)
    {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.member = new SimpleStringProperty(member);
        this.issued_by = new SimpleStringProperty(issued_by);
        this.issued_date = new SimpleStringProperty(issued_date);
        this.status = new SimpleStringProperty(status);
    }

    public SimpleStringProperty idProperty() {return this.id;}
    public SimpleStringProperty titleProperty() {return this.title;}
    public SimpleStringProperty memberProperty() {return this.member;}
    public SimpleStringProperty issuedByProperty() {return this.issued_by;}
    public SimpleStringProperty issuedDateProperty() {return this.issued_date;}
    public SimpleStringProperty statusProperty() {return this.status;}

}

public class DashHomeController
{
    //Card Holder
    @FXML
    private HBox cardHolder;

    //Card Labels
    @FXML
    private Label tb_count;
    @FXML
    private Label mb_count;
    @FXML
    private Label is_count;
    @FXML
    private Label ob_count;

    //TreeTableView
    @FXML
    private TreeTableView<ActivityItem> activityTableView;

    @FXML
    private TreeTableColumn<ActivityItem,String> idCol;
    @FXML
    private TreeTableColumn<ActivityItem,String> titleCol;
    @FXML
    private TreeTableColumn<ActivityItem,String> memCol;
    @FXML
    private TreeTableColumn<ActivityItem,String> issuedByCol;
    @FXML
    private TreeTableColumn<ActivityItem,String> issuedDateCol;
    @FXML
    private TreeTableColumn<ActivityItem,String> statusCol;


    @FXML
    public void initialize()
    {
        initTransitions();
        updateStats();
        initTableView();
        loadRecentActivity();
    }

    protected void initTransitions()
    {
        List<Node> cards = cardHolder.getChildren().stream()
                .filter(n-> n.getStyleClass().contains("home-info-container"))
                .toList();

        for (Node node:cards)
        {
            ScaleTransition st = new ScaleTransition(Duration.seconds(0.5),node);
            node.setOnMouseEntered(e->{
                st.setToX(1.03);
                st.setToY(1.03);
                st.playFromStart();
            });

            node.setOnMouseExited(e->{
                st.setToX(1);
                st.setToY(1);
                st.playFromStart();
            });

        }
    }

    protected void initTableView()
    {
        //Init Row Factory
        activityTableView.setRowFactory(tv -> new TreeTableRow<ActivityItem>(){
            @Override
            protected void updateItem(ActivityItem item,boolean empty)
            {
                super.updateItem(item,empty);
                getStyleClass().remove("inactive-row");
                getStyleClass().remove("active-row");
                if ((item!= null || !empty) && item.statusProperty().get().equals("OVERDUE"))
                {
                    getStyleClass().add("inactive-row");
                }
                else if((item!= null || !empty) && item.statusProperty().get().equals("RETURNED"))
                {
                    getStyleClass().add("active-row");
                }
            }
        });

        //Init Cell Factory
        idCol.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        titleCol.setCellValueFactory(p -> p.getValue().getValue().titleProperty());
        memCol.setCellValueFactory(p -> p.getValue().getValue().memberProperty());
        issuedByCol.setCellValueFactory(p -> p.getValue().getValue().issuedByProperty());
        issuedDateCol.setCellValueFactory(p -> p.getValue().getValue().issuedDateProperty());
        statusCol.setCellValueFactory(p -> p.getValue().getValue().statusProperty());

        //Set Root
        TreeItem<ActivityItem> root = new TreeItem<>(new ActivityItem("","","","","",""));
        root.setExpanded(false);
        activityTableView.setShowRoot(false);
        activityTableView.setRoot(root);
    }

    protected void updateStats()
    {
        String query = "SELECT " +
                "(SELECT COUNT(*) FROM BOOKS) as TOTAL_BOOKS," +
                "(SELECT COUNT(*) FROM MEMBERS) as TOTAL_MEMBERS," +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS='ISSUED') as TOTAL_ISSUES," +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS='OVERDUE') as TOTAL_OVERDUE;";
        List<List<String>> result = Util.executeSQLQuery(query,new String[]{"TOTAL_BOOKS","TOTAL_MEMBERS","TOTAL_ISSUES","TOTAL_OVERDUE"});
        if(!result.isEmpty())
        {
            tb_count.setText(result.get(0).get(0));
            mb_count.setText(result.get(0).get(1));
            is_count.setText(result.get(0).get(2));
            ob_count.setText(result.get(0).get(3));

        }
    }

    protected void loadRecentActivity()
    {
        String query = "SELECT " +
                "i.ID," +
                "b.TITLE AS TITLE," +
                "m.NAME AS NAME," +
                "i.LIBRARIAN," +
                "i.ISSUE_DATE," +
                "i.STATUS " +
                "FROM LOGS i " +
                "JOIN BOOKS b ON i.BOOK_ID = b.ID " +
                "JOIN MEMBERS m ON i.MEMBER_ID = m.ID ORDER BY i.ID DESC LIMIT 15;";
        List<List<String>> result = Util.executeSQLQuery(query,new String[]{"ID","TITLE","NAME","LIBRARIAN","ISSUE_DATE","STATUS"});
        if(!result.isEmpty())
        {
            activityTableView.getRoot().getChildren().clear();
            for(List<String> row:result)
            {
                TreeItem<ActivityItem> item = new TreeItem<>(new ActivityItem(row.get(0), row.get(1), row.get(2),row.get(3),row.get(4),row.get(5)));
                activityTableView.getRoot().getChildren().add(item);
            }
        }
    }
}
