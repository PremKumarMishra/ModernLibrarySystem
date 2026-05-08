package com.dsa.modernlibrarysystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RegisterController
{
    @FXML
    private VBox registerVBox;
    @FXML
    private TextField regUserField;
    @FXML
    private TextField regEmailField;
    @FXML
    private TextField regPasswordField;
    @FXML
    private TextField regConfirmPasswordField;
    
    @FXML
    protected void registerAdmin()
    {
        if (regPasswordField.getText().length() < 6)
        {
            AlertDialog.show("Password Error","Entered password must be at least 6 digits long",AlertDialog.AlertType.ERROR);
        }
        else
        {
            if (regPasswordField.getText().equals(regConfirmPasswordField.getText()))
            {
                String sqlQuery = "SELECT EMAIL FROM ADMINS WHERE NAME=\"%s\"".formatted(regUserField.getText());
                List<List<String>> result = Util.executeSQLQuery(sqlQuery,null);
                if(result.isEmpty())
                {
                    String passwordHash = Util.getSha256Hash(regConfirmPasswordField.getText());
                    if(passwordHash.isBlank() || regUserField.getText().isBlank() || regEmailField.getText().isBlank())
                    {
                        AlertDialog.show("Hashing Error","Failed to generate password hash",AlertDialog.AlertType.ERROR);
                    }
                    else
                    {
                        sqlQuery = "INSERT INTO ADMINS(NAME,EMAIL,PASSWORD) VALUES(\"%s\",\"%s\",\"%s\");".formatted(regUserField.getText(),regEmailField.getText(),passwordHash);
                        Util.updateSQLQuery(sqlQuery);
                        AlertDialog.show("Registration Successful","User registration is successful, you may now login to your account",AlertDialog.AlertType.INFORMATION);
                    }
                }
                else
                {
                    AlertDialog.show("Account Error","User is already registered in the database",AlertDialog.AlertType.WARNING);
                }
            }
            else
            {
                AlertDialog.show("Password Error","Confirm password field does not match with the password field",AlertDialog.AlertType.ERROR);
            }
        }
    }
    @FXML
    protected void returnToLogin()
    {
        SceneManager.switchView("Views/LoginView.fxml");
    }
}
