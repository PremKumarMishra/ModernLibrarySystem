package com.dsa.modernlibrarysystem;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class IssuedBookItem
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty member;
    private final SimpleStringProperty issuer;
    private final SimpleStringProperty issueDate;
    private final SimpleStringProperty dueDate;
    private final SimpleStringProperty returnDate;
    private final SimpleStringProperty status;

    public IssuedBookItem(String id,String title,String author,String member,String issuer,String issueDate,String dueDate,String returnDate,String status)
    {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.member = new SimpleStringProperty(member);
        this.issuer = new SimpleStringProperty(issuer);
        this.issueDate = new SimpleStringProperty(issueDate);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.status= new SimpleStringProperty(status);

    }

    public SimpleStringProperty idProperty(){return id;}
    public SimpleStringProperty titleProperty(){return title;}
    public SimpleStringProperty authorProperty(){return author;}
    public SimpleStringProperty memberProperty(){return member;}
    public SimpleStringProperty issuerProperty(){return issuer;}
    public SimpleStringProperty issueDateProperty(){return issueDate;}
    public SimpleStringProperty dueDateProperty(){return dueDate;}
    public SimpleStringProperty returnDateProperty(){return returnDate;}
    public SimpleStringProperty statusProperty(){return status;}
}

class ReturnBookItem
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty member;
    private final SimpleStringProperty issueDate;
    private final SimpleStringProperty dueDate;
    private final SimpleStringProperty status;

    public ReturnBookItem(String id,String title,String member,String issueDate,String dueDate,String status)
    {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.member = new SimpleStringProperty(member);
        this.issueDate = new SimpleStringProperty(issueDate);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.status= new SimpleStringProperty(status);

    }

    public SimpleStringProperty idProperty(){return id;}
    public SimpleStringProperty titleProperty(){return title;}
    public SimpleStringProperty memberProperty(){return member;}
    public SimpleStringProperty issueDateProperty(){return issueDate;}
    public SimpleStringProperty dueDateProperty(){return dueDate;}
    public SimpleStringProperty statusProperty(){return status;}
}


public class DashIssueReturnView
{
    //Tab1
    @FXML
    private ComboBox<String> bookCombobox;
    @FXML
    private  ComboBox<String> memberCombobox;
    @FXML
    private TextField dueDateField;

    //Tab3
    @FXML
    private TreeTableView<IssuedBookItem> issueTableView;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueIdCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueTitleCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueAuthorCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueMemberCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueIssuedCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueIssueDateCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueDueDateCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueReturnDateCol;
    @FXML
    private TreeTableColumn<IssuedBookItem, String> issueStatusCol;

    //Tab2
    @FXML
    private TreeTableView<ReturnBookItem> returnTableView;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueIdCol;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueTitleCol;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueMemberCol;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueIssueDateCol;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueDueDateCol;
    @FXML
    private TreeTableColumn<ReturnBookItem, String> rIssueStatusCol;
    @FXML
    private Button overDueRemBt;
    @FXML
    private TextField searchField;

    private boolean isBookSelecting = false;
    private boolean isMemberSelecting = false;

    @FXML
    public void initialize()
    {
        initBookCombo();
        initMemberCombo();
        initIssueTreeTable();
        initReturnTreeTable();

    }

    protected void initBookCombo()
    {
        bookCombobox.getEditor().textProperty().addListener((observable,oldVal,newVal) -> {
            if(!isBookSelecting)
            {
                searchBook();
            }
            isBookSelecting = false;
        });
        bookCombobox.setOnAction( e -> {
            isBookSelecting = true;
        });
        ComboBoxListViewSkin<String> skin = new ComboBoxListViewSkin<String>(bookCombobox);
        skin.getPopupContent().addEventFilter(KeyEvent.ANY, e -> {
            if (e.getCode() == KeyCode.SPACE)
            {
                e.consume();
            }
        });
        bookCombobox.setSkin(skin);
    }

    protected void initMemberCombo()
    {
        memberCombobox.getEditor().textProperty().addListener((observable, olVal, newVal) -> {
            if(!isMemberSelecting)
            {
                searchMember();
            }
            isMemberSelecting = false;
        });

        memberCombobox.setOnAction(e -> {
            isMemberSelecting = true;
        });

        ComboBoxListViewSkin<String> memSkin = new ComboBoxListViewSkin<String>(memberCombobox);
        memSkin.getPopupContent().addEventFilter(KeyEvent.ANY,e -> {
            if(e.getCode() == KeyCode.SPACE)
            {
                e.consume();
            }
        });
        memberCombobox.setSkin(memSkin);
    }

    protected void initReturnTreeTable()
    {
        //Add Listener To Text Field
        searchField.textProperty().addListener(((observable, oldVal, newVal) -> searchIssuedBook(newVal)));

        //Init Row Factory
        returnTableView.setRowFactory(tv -> new TreeTableRow<ReturnBookItem>(){
            @Override
            protected void updateItem(ReturnBookItem item,boolean empty)
            {
                super.updateItem(item,empty);
                getStyleClass().remove("inactive-row");
                if ((item!= null || !empty) && item.statusProperty().get().equals("OVERDUE"))
                {
                    getStyleClass().add("inactive-row");
                }
            }
        });

        //Init Cell Values
        rIssueIdCol.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        rIssueTitleCol.setCellValueFactory(p -> p.getValue().getValue().titleProperty());
        rIssueMemberCol.setCellValueFactory(p -> p.getValue().getValue().memberProperty());
        rIssueIssueDateCol.setCellValueFactory(p -> p.getValue().getValue().issueDateProperty());
        rIssueDueDateCol.setCellValueFactory(p -> p.getValue().getValue().dueDateProperty());
        rIssueStatusCol.setCellValueFactory(p -> p.getValue().getValue().statusProperty());
        //Set Root
        TreeItem<ReturnBookItem> root = new TreeItem<ReturnBookItem>(new ReturnBookItem("","","","","",""));
        root.setExpanded(false);
        returnTableView.setShowRoot(false);
        returnTableView.setRoot(root);
        //Load Return Book Data
        loadReturnBookData();
    }

    protected void initIssueTreeTable()
    {
        //Init Row Factory
        issueTableView.setRowFactory(tv -> new TreeTableRow<IssuedBookItem>(){
             @Override
             protected void updateItem(IssuedBookItem item,boolean empty)
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

        //Init Cell Values
        issueIdCol.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        issueTitleCol.setCellValueFactory(p -> p.getValue().getValue().titleProperty());
        issueAuthorCol.setCellValueFactory(p -> p.getValue().getValue().authorProperty());
        issueMemberCol.setCellValueFactory(p -> p.getValue().getValue().memberProperty());
        issueIssuedCol.setCellValueFactory(p -> p.getValue().getValue().issuerProperty());
        issueIssueDateCol.setCellValueFactory(p -> p.getValue().getValue().issueDateProperty());
        issueDueDateCol.setCellValueFactory(p -> p.getValue().getValue().dueDateProperty());
        issueReturnDateCol.setCellValueFactory(p -> p.getValue().getValue().returnDateProperty());
        issueStatusCol.setCellValueFactory(p -> p.getValue().getValue().statusProperty());
        //Set Root
        TreeItem<IssuedBookItem> root = new TreeItem<IssuedBookItem>(new IssuedBookItem("","","","","","","","",""));
        root.setExpanded(false);
        issueTableView.setShowRoot(false);
        issueTableView.setRoot(root);
        //Load Return Book Data
        loadIssuedBookData();
    }

    @FXML
    protected void issueBook()
    {
        List<List<String>> r1 = Util.executeSQLQuery("SELECT ID,AVAILABLE FROM BOOKS WHERE TITLE = '%s';".formatted(bookCombobox.getEditor().getText()),new String[]{"ID","AVAILABLE"});
        List<List<String>> r2 = Util.executeSQLQuery("SELECT ID FROM MEMBERS WHERE NAME = '%s';".formatted(memberCombobox.getEditor().getText()),new String[]{"ID"});

        String librarian = Util.getSystemUser();
        LocalDate today = LocalDate.now().plusDays(Integer.parseInt(dueDateField.getText()));
        String formattedDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        int bookAvailable;

        if (!r1.isEmpty() && !r2.isEmpty())
        {
            bookAvailable = Integer.parseInt(r1.get(0).get(1));
            if(bookAvailable<1)
            {
                AlertDialog.show("Issue Failed","The book is no longer available in the library",AlertDialog.AlertType.WARNING);
                return;
            }

            List<List<String>> r3 = Util.executeSQLQuery("SELECT ID FROM LOGS WHERE BOOK_ID=%d AND MEMBER_ID=%d;".formatted(Integer.parseInt(r1.get(0).get(0)),Integer.parseInt(r2.get(0).get(0))),new String[]{"ID"});
            if (r3.isEmpty())
            {
                Util.updateSQLQuery("INSERT INTO LOGS(BOOK_ID,MEMBER_ID,LIBRARIAN,DUE_DATE) VALUES(%d,%d,'%s','%s');".formatted(Integer.parseInt(r1.get(0).get(0)),Integer.parseInt(r2.get(0).get(0)),librarian,formattedDate));
                Util.updateSQLQuery("UPDATE BOOKS SET AVAILABLE=AVAILABLE-1 WHERE ID=%d;".formatted(Integer.parseInt(r1.get(0).get(0))));
                AlertDialog.show("Issue Successfull","Book has been successfully issued to the user",AlertDialog.AlertType.INFORMATION);
            }
            else
            {
                AlertDialog.show("Issue Error","The book was already issued to the specified member" ,AlertDialog.AlertType.ERROR);
            }
        }
        else
        {
            AlertDialog.show("Database Error","The specified member or book is not found in database" ,AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void returnBook()
    {
        if (!returnTableView.getSelectionModel().isEmpty())
        {
            TreeItem<ReturnBookItem> item = returnTableView.getSelectionModel().getSelectedItem();
            if(item != null)
            {
                Util.updateSQLQuery("UPDATE LOGS SET STATUS='RETURNED',RETURN='%s' WHERE ID=%d;".formatted(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),Integer.parseInt(item.getValue().idProperty().getValue())));
                List<List<String>> bookResult = Util.executeSQLQuery("SELECT ID FROM BOOKS WHERE TITLE='%s';".formatted(item.getValue().titleProperty().getValue()),null);
                if(!bookResult.isEmpty())
                {
                    int bookID = Integer.parseInt(bookResult.get(0).get(0));
                    Util.updateSQLQuery("UPDATE BOOKS SET AVAILABLE = AVAILABLE+1 WHERE ID=%d;".formatted(bookID));
                }
                returnTableView.getRoot().getChildren().remove(item);
            }
        }
    }

    protected void searchMember()
    {
        String newVal = memberCombobox.getEditor().getText();
        if(!newVal.isBlank())
        {
            List<List<String>> result = Util.executeSQLQuery("SELECT NAME FROM MEMBERS WHERE NAME LIKE '%%%s%%' LIMIT 10;".formatted(newVal),new String[]{"NAME"});
            Platform.runLater(() -> {
                memberCombobox.getItems().setAll(result.stream().map(row -> row.get(0)).toList());
                memberCombobox.getEditor().setText(newVal);
                memberCombobox.getEditor().positionCaret(newVal.length());
                if(!result.isEmpty())
                {
                    memberCombobox.show();
                }
                else
                {
                    memberCombobox.hide();
                }
            });
        }
    }

    protected void searchBook()
    {
        String newVal = bookCombobox.getEditor().getText();
        if (!newVal.isBlank())
        {
//            bookCombobox.getItems().clear();
            List<List<String>> result = Util.executeSQLQuery("SELECT TITLE FROM BOOKS WHERE TITLE LIKE '%%%s%%' LIMIT 20;".formatted(newVal.replace("'","''")),new String[]{"TITLE"});
            Platform.runLater(() -> {
                bookCombobox.getItems().setAll(result.stream().map(row -> row.get(0)).toList());
                bookCombobox.getEditor().setText(newVal);
                bookCombobox.getEditor().positionCaret(newVal.length());
                if(!result.isEmpty())
                {
                    bookCombobox.show();
                }
                else
                {
                    bookCombobox.hide();
                }
            });
        }

    }

    protected void searchIssuedBook(String newVal)
    {
        if(!newVal.isBlank())
        {
            List<TreeItem<ReturnBookItem>> itemList = new ArrayList<>();
            returnTableView.getRoot().getChildren().forEach(item -> {
                if(item.getValue().titleProperty().getValue().toLowerCase().startsWith(newVal.toLowerCase()))
                {
                    itemList.add(item);
                }
            });

            //Show Search Results
            returnTableView.getRoot().getChildren().clear();
            returnTableView.getRoot().getChildren().setAll(itemList.stream().toList());

        }
        else
        {
            refreshReturnedBooks();;
        }
    }

    protected void loadReturnBookData()
    {
        String query = "SELECT " +
                "i.ID," +
                "b.TITLE AS TITLE," +
                "m.NAME AS NAME," +
                "i.ISSUE_DATE," +
                "i.DUE_DATE," +
                "i.STATUS " +
                "FROM LOGS i " +
                "JOIN BOOKS b ON i.BOOK_ID = b.ID " +
                "JOIN MEMBERS m ON i.MEMBER_ID = m.ID "+
                "WHERE i.STATUS != 'RETURNED';";
        List<List<String>> result = Util.executeSQLQuery(query,new String[]{"ID","TITLE","NAME","ISSUE_DATE","DUE_DATE","STATUS"});
        if(!result.isEmpty())
        {
            for(List<String> row:result)
            {
                LocalDate d1 = LocalDate.now();
                LocalDate d2 = LocalDate.parse(row.get(4));
                if(d1.isAfter(d2))
                {
                    row.set(5,"OVERDUE");
                    Util.updateSQLQuery("UPDATE LOGS SET STATUS = 'OVERDUE' WHERE ID = %d".formatted(Integer.parseInt(row.get(0))));
                }
                TreeItem<ReturnBookItem> item = new TreeItem<>(new ReturnBookItem(
                        row.get(0),row.get(1), row.get(2),
                        row.get(3),row.get(4),row.get(5)));
                returnTableView.getRoot().getChildren().add(item);
            }
        }
    }

    protected void loadIssuedBookData()
    {
        String query = "SELECT " +
                "i.ID," +
                "b.TITLE AS TITLE," +
                "b.AUTHOR AS AUTHOR," +
                "m.NAME AS NAME," +
                "i.LIBRARIAN," +
                "i.ISSUE_DATE," +
                "i.DUE_DATE," +
                "i.RETURN," +
                "i.STATUS " +
                "FROM LOGS i " +
                "JOIN BOOKS b ON i.BOOK_ID = b.ID " +
                "JOIN MEMBERS m ON i.MEMBER_ID = m.ID;";
        List<List<String>> result = Util.executeSQLQuery(query,new String[]{"ID","TITLE","AUTHOR","NAME","LIBRARIAN","ISSUE_DATE","DUE_DATE","RETURN","STATUS"});
        if(!result.isEmpty())
        {
            for(List<String> row:result)
            {
                LocalDate d1 = LocalDate.now();
                LocalDate d2 = LocalDate.parse(row.get(6));
                if(d1.isAfter(d2))
                {
                    row.set(8,"OVERDUE");
                    Util.updateSQLQuery("UPDATE LOGS SET STATUS = 'OVERDUE' WHERE ID = %d".formatted(Integer.parseInt(row.get(0))));
                }
                TreeItem<IssuedBookItem> item = new TreeItem<>(new IssuedBookItem(
                        row.get(0),row.get(1), row.get(2),
                        row.get(3),row.get(4),row.get(5),
                        row.get(6),row.get(7),row.get(8)));
                issueTableView.getRoot().getChildren().add(item);
            }
        }
    }


    @FXML
    protected void sendOverDueReminders()
    {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                issueTableView.getRoot().getChildren().forEach(item -> {
                    if (item.getValue().statusProperty().getValue().equals("OVERDUE"))
                    {
                        List<List<String>> result = Util.executeSQLQuery("SELECT EMAIL FROM MEMBERS WHERE NAME='%s'".formatted(item.getValue().memberProperty().getValue()),null);
                        if (!result.isEmpty())
                        {
                            String email =  result.get(0).get(0);
                            System.out.println(email);
                            String body = Util.getOverdueMailText(item.getValue().titleProperty().getValue(),
                                    item.getValue().authorProperty().getValue(),item.getValue().memberProperty().getValue(),
                                    item.getValue().issuerProperty().getValue(),item.getValue().issueDateProperty().getValue(),
                                    item.getValue().dueDateProperty().getValue());
                            boolean succ = Util.sendMail("Overdue Book Reminder",body,email);
                            if(!succ)
                            {
                                AlertDialog.show("Email Error","There was an error while sending overdue reminders",AlertDialog.AlertType.ERROR);
                            }
                        }
                    }
                });
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            overDueRemBt.setGraphic(null);
            overDueRemBt.setDisable(false);
        });
        new Thread(task).start();

        //Aftermaths
        Image loadImg = new Image(getClass().getResourceAsStream("Assets/Icons/loading.gif"));
        ImageView loadView = new ImageView(loadImg);
        loadView.setFitHeight(16);
        loadView.setFitWidth(16);
        overDueRemBt.setGraphic(loadView);
        overDueRemBt.setDisable(true);

    }

    @FXML
    protected void refreshIssuedBooks()
    {
        issueTableView.getRoot().getChildren().clear();
        loadIssuedBookData();
    }

    @FXML
    protected void refreshReturnedBooks()
    {
        returnTableView.getRoot().getChildren().clear();
        loadReturnBookData();
    }

}
