package com.dsa.modernlibrarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemberModalController
{
    @FXML
    protected VBox modalBox;
    @FXML
    protected TextField userNameField;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField regdField;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    protected void addMember()
    {
        if (!userNameField.getText().isBlank() && !emailField.getText().isBlank() && !regdField.getText().isBlank())
        {
            if (!Util.isValidREGDNumber(regdField.getText()))
            {
                AlertDialog.show("REGD Error","Please enter a valid registration number",AlertDialog.AlertType.WARNING);
            }
            else
            {
                String sqlQuery = "INSERT INTO MEMBERS(NAME,EMAIL,REGD,YEAR) VALUES(\"%s\",\"%s\",\"%s\",2);".formatted(userNameField.getText(),emailField.getText(),regdField.getText());
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
