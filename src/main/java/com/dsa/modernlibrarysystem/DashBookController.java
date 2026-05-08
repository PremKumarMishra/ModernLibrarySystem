package com.dsa.modernlibrarysystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class BookItem
{
    private final SimpleStringProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty category;
    private final SimpleStringProperty isbn;
    private final SimpleStringProperty total;
    private final SimpleStringProperty available;
    private final SimpleStringProperty pubYear;
    private final SimpleStringProperty entryDate;

    public BookItem(String id,String title,String author,String category,String isbn,String total,String available,String pubYear,String entryDate)
    {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.category = new SimpleStringProperty(category);
        this.isbn = new SimpleStringProperty(isbn);
        this.total = new SimpleStringProperty(total);
        this.available = new SimpleStringProperty(available);
        this.pubYear = new SimpleStringProperty(pubYear);
        this.entryDate = new SimpleStringProperty(entryDate);

    }

    public SimpleStringProperty idProperty() {return this.id;}
    public SimpleStringProperty titleProperty() {return this.title;}
    public SimpleStringProperty authorProperty() {return this.author;}
    public SimpleStringProperty categoryProperty() {return this.category;}
    public SimpleStringProperty isbnProperty() {return this.isbn;}
    public SimpleStringProperty totalProperty() {return this.total;}
    public SimpleStringProperty availableProperty() {return this.available;}
    public SimpleStringProperty pubYearProperty() {return this.pubYear;}
    public SimpleStringProperty entryDateProperty() {return this.entryDate;}
}

public class DashBookController
{
    @FXML
    protected TextField searchField;
    @FXML
    protected TreeTableView<BookItem> bookTableView;
    @FXML
    protected TreeTableColumn<BookItem,String> idCol;
    @FXML
    protected TreeTableColumn<BookItem,String> titleCol;
    @FXML
    protected TreeTableColumn<BookItem,String> authorCol;
    @FXML
    protected TreeTableColumn<BookItem,String> categoryCol;
    @FXML
    protected TreeTableColumn<BookItem,String> isbnCol;
    @FXML
    protected TreeTableColumn<BookItem,String> totalCol;
    @FXML
    protected TreeTableColumn<BookItem,String> availCol;
    @FXML
    protected TreeTableColumn<BookItem,String> pubCol;
    @FXML
    protected TreeTableColumn<BookItem,String> entryCol;

    @FXML
    public void initialize()
    {
//        //Setup Search Listener
        searchField.textProperty().addListener((observable,oldVa,newVal) -> onSearch(newVal));

        //INIT Cell Factory
        idCol.setCellValueFactory(p->p.getValue().getValue().idProperty());
        titleCol.setCellValueFactory(p->p.getValue().getValue().titleProperty());
        authorCol.setCellValueFactory(p->p.getValue().getValue().authorProperty());
        categoryCol.setCellValueFactory(p->p.getValue().getValue().categoryProperty());
        isbnCol.setCellValueFactory(p->p.getValue().getValue().isbnProperty());
        totalCol.setCellValueFactory(p->p.getValue().getValue().totalProperty());
        availCol.setCellValueFactory(p->p.getValue().getValue().availableProperty());
        pubCol.setCellValueFactory(p->p.getValue().getValue().pubYearProperty());
        entryCol.setCellValueFactory(p->p.getValue().getValue().entryDateProperty());

        //Set TreeTableView Root
        TreeItem<BookItem> root = new TreeItem<>(new BookItem("","","","","","","","",""));
        root.setExpanded(false);
        bookTableView.setRoot(root);
        bookTableView.setShowRoot(false);
        //Load Table Data
        loadBooksList();
    }

    @FXML
    protected void addBook()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("modals/BooksModal.fxml"));
            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.initStyle(StageStyle.TRANSPARENT);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(bookTableView.getScene().getWindow());
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
    protected void editBook()
    {
        try
        {
            if (!bookTableView.getSelectionModel().isEmpty())
            {
                TreeItem<BookItem> item = bookTableView.getSelectionModel().getSelectedItem();
                if(item != null)
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("modals/BooksEditModal.fxml"));
                    Parent root = loader.load();
                    BookEditModalController controller = loader.getController();
                    controller.initValues(item.getValue().titleProperty().get(),item.getValue().authorProperty().get(),item.getValue().categoryProperty().get(),item.getValue().isbnProperty().get(),item.getValue().totalProperty().get(),item.getValue().pubYearProperty().get(),Integer.parseInt(item.getValue().idProperty().get()));
                    Stage modal = new Stage();
                    modal.setScene(new Scene(root));
                    modal.initStyle(StageStyle.TRANSPARENT);
                    modal.initModality(Modality.APPLICATION_MODAL);
                    modal.initOwner(bookTableView.getScene().getWindow());
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
    protected void deleteBook()
    {
        if (!bookTableView.getSelectionModel().isEmpty())
        {
            TreeItem<BookItem> item = bookTableView.getSelectionModel().getSelectedItem();
            if (item != null)
            {
                boolean res = AlertDialog.show("Delete Confirmation","Are you sure you want to remove this book ?",AlertDialog.AlertType.CONFIRMATION);
                if(res)
                {
                    int bookID = Integer.parseInt(item.getValue().idProperty().getValue());
                    Util.updateSQLQuery("DELETE FROM BOOKS WHERE ID = %d".formatted(bookID));
                    Util.updateSQLQuery("DELETE FROM LOGS WHERE BOOK_ID = %d".formatted(bookID));
                    bookTableView.getRoot().getChildren().remove(item);
                }

            }
        }
    }

    @FXML
    protected void refreshList()
    {
        bookTableView.getRoot().getChildren().clear();
        loadBooksList();
    }

    @FXML
    protected void loadBooksList()
    {
        List<List<String>> result = Util.executeSQLQuery("SELECT ID,TITLE,AUTHOR,CATEGORY,ISBN,TOTAL,AVAILABLE,PUB_YEAR,ENTRY_DATE FROM BOOKS;",new String[]{"ID","TITLE","AUTHOR","CATEGORY","ISBN","TOTAL","AVAILABLE","PUB_YEAR","ENTRY_DATE"});
        if (!result.isEmpty())
        {
            for(List<String>rowData : result)
            {
                TreeItem<BookItem> item = new TreeItem<>(new BookItem(rowData.get(0),rowData.get(1),rowData.get(2),rowData.get(3),rowData.get(4),rowData.get(5),rowData.get(6),rowData.get(7),rowData.get(8)));
                bookTableView.getRoot().getChildren().add(item);
            }
        }
    }

    @FXML
    protected void onSearch(String newVal)
    {
        if (!newVal.isBlank())
        {
            List<TreeItem<BookItem>> searchRes = new ArrayList<>();
            bookTableView.getRoot().getChildren().forEach(child -> {
                if(child.getValue().titleProperty().get().toLowerCase().startsWith(newVal.toLowerCase()) || child.getValue().authorProperty().get().toLowerCase().startsWith(newVal.toLowerCase()))
                {
                    searchRes.add(child);
                }
            });
            //Show Search Results
            bookTableView.getRoot().getChildren().clear();
            for(TreeItem<BookItem> child : searchRes)
            {
                bookTableView.getRoot().getChildren().add(child);
            }
        }
        else
        {
            bookTableView.getRoot().getChildren().clear();
            loadBooksList();
        }
    }

}

