package com.dsa.modernlibrarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class BookModalController
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

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    protected void addBook()
    {
        if (!titleField.getText().isBlank() && !authorField.getText().isBlank() && !categoryField.getText().isBlank() && !isbnField.getText().isBlank() && !totalField.getText().isBlank() && !pubYearField.getText().isBlank())
        {
            if (Util.doesBookExists(titleField.getText()))
            {
                AlertDialog.show("Duplicate Book","Book already exists in the database",AlertDialog.AlertType.WARNING);
            }
            else
            {
                String sqlQuery = String.format(
                        "INSERT INTO BOOKS(TITLE,AUTHOR,CATEGORY,ISBN,TOTAL,AVAILABLE,PUB_YEAR) " +
                                "VALUES('%s','%s','%s','%s',%d,%d,'%s');",
                        titleField.getText(),  // TITLE
                        authorField.getText(),  // AUTHOR
                        categoryField.getText(),  // CATEGORY
                        isbnField.getText(),  // ISBN
                        Integer.parseInt(totalField.getText()), // TOTAL
                        Integer.parseInt(totalField.getText()), // AVAILABLE
                        pubYearField.getText()  // PUB_YEAR
                );
                Util.updateSQLQuery(sqlQuery);
                Stage modal = (Stage) modalBox.getScene().getWindow();
                modal.close();;
            }

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
