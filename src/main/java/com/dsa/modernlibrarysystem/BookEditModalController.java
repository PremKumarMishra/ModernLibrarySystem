package com.dsa.modernlibrarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookEditModalController
{
    @FXML
    protected VBox modalBox;
    @FXML
    protected TextField titleField;
    @FXML
    protected TextField authorField;
    @FXML
    protected TextField categoryField;
    @FXML
    protected TextField isbnField;
    @FXML
    protected TextField totalField;
    @FXML
    protected TextField pubYearField;

    private int bookID;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initValues(String title,String author,String category,String isbn,String total,String pubYear,int ID)
    {
        this.titleField.setText(title);
        this.authorField.setText(author);
        this.categoryField.setText(category);
        this.isbnField.setText(isbn);
        this.totalField.setText(total);
        this.pubYearField.setText(pubYear);

        this.bookID = ID;
    }

    @FXML
    protected void updateBook()
    {
        if (!titleField.getText().isBlank() && !authorField.getText().isBlank() && !categoryField.getText().isBlank() && !isbnField.getText().isBlank() && !totalField.getText().isBlank() && !pubYearField.getText().isBlank())
        {
            String sqlQuery = String.format(
                    "UPDATE BOOKS SET TITLE = '%s',AUTHOR = '%s',CATEGORY = '%s',ISBN = '%s',TOTAL = %d,PUB_YEAR = '%s' WHERE ID = %d;",
                    titleField.getText(),  // TITLE
                    authorField.getText(),  // AUTHOR
                    categoryField.getText(),  // CATEGORY
                    isbnField.getText(),  // ISBN
                    Integer.parseInt(totalField.getText()), // TOTAL
                    pubYearField.getText(),  // PUB_YEAR
                    bookID //BOOK ID
                    );
            Util.updateSQLQuery(sqlQuery);
            Stage modal = (Stage) modalBox.getScene().getWindow();
            modal.close();

        }
        else
        {
            AlertDialog.show("Credentials Error","Please fill in the required details",AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void closeModal(ActionEvent e)
    {
        Stage modal = (Stage) ((Node)e.getSource()).getScene().getWindow();
        modal.close();
    }

    @FXML
    protected void onMousePressed(MouseEvent e)
    {
        Stage modal = (Stage) modalBox.getScene().getWindow();

        xOffset = e.getScreenX() - modal.getX();
        yOffset = e.getScreenY() - modal.getY();
    }

    @FXML
    protected void onMouseDragged(MouseEvent e)
    {
        Stage modal = (Stage) modalBox.getScene().getWindow();
        modal.setX(e.getScreenX() - xOffset);
        modal.setY(e.getScreenY() - yOffset);
    }
}
