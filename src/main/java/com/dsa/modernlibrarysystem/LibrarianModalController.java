package com.dsa.modernlibrarysystem;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibrarianModalController
{
    @FXML
    protected VBox modalBox;
    @FXML
    protected TextField userNameField;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField passwordField;
    @FXML
    protected TextField confirmPasswordField;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    protected void addLibrarian()
    {
        if (!userNameField.getText().isBlank() && !emailField.getText().isBlank())
        {
            if (passwordField.getText().length() >=6 )
            {
                if (passwordField.getText().equals(confirmPasswordField.getText()))
                {
                    String passwordHash = Util.getSha256Hash(passwordField.getText());
                    if (!passwordHash.isBlank())
                    {
                        String sqlQuery = "INSERT INTO ADMINS(NAME,EMAIL,PASSWORD) VALUES(\"%s\",\"%s\",\"%s\");".formatted(userNameField.getText(),emailField.getText(),passwordHash);
                        Util.updateSQLQuery(sqlQuery);
                        Stage modal = (Stage) modalBox.getScene().getWindow();
                        modal.close();;
                    }
                    else
                    {
                        AlertDialog.show("Hashing Error","Failed to generate password hash",AlertDialog.AlertType.ERROR);
                    }
                }
                else
                {
                    AlertDialog.show("Password Error","Confirm password field does not match with the password field",AlertDialog.AlertType.ERROR);
                }
            }
            else
            {
                AlertDialog.show("Password Error","Entered password must be at least 6 digits long",AlertDialog.AlertType.ERROR);
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
