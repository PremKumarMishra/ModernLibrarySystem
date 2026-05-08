package com.dsa.modernlibrarysystem;
import jakarta.mail.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class LoginController
{
    @FXML
    private TextField userField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button forgotPassBt;

    @FXML
    protected void loginToSystem()
    {
        if(!userField.getText().isBlank() && !passwordField.getText().isBlank())
        {
            String sqlQuery = "SELECT PASSWORD FROM ADMINS WHERE NAME=\"%s\" AND STATUS='ACTIVE';".formatted(userField.getText());
            List<List<String>> result = Util.executeSQLQuery(sqlQuery,null);
            if (!result.isEmpty())
            {
                List<String> rowData = result.get(0);
                String storedHash = rowData.get(0);
                String passwordHash = Util.getSha256Hash(passwordField.getText());
                if(!passwordHash.isBlank())
                {
                    if (storedHash.equals(passwordHash))
                    {
                        Util.updateSQLQuery("INSERT INTO ADMIN_LOGS(NAME) VALUES (\"%s\");".formatted(userField.getText()));
                        Util.setSystemUser(userField.getText());
                        SceneManager.switchView("Views/DashboardView.fxml");
                    }
                    else
                    {
                        AlertDialog.show("Password Error","Please enter correct password",AlertDialog.AlertType.ERROR);
                    }
                }
                else
                {
                    AlertDialog.show("Hashing Error","Failed to generate password hash",AlertDialog.AlertType.ERROR);
                }
            }
            else
            {
                AlertDialog.show("Account Error","User is not registered in the database",AlertDialog.AlertType.ERROR);
            }

        }
        else
        {
            AlertDialog.show("Credentials Empty","Please fill in username and password",AlertDialog.AlertType.ERROR);
        }
    }
    @FXML
    protected void goToRegisterBox()
    {
        SceneManager.switchView("Views/RegisterView.fxml");
    }

    @FXML
    protected void sendPassToMail()
    {
        String receiverMail;

        //"alishadas824@gmail.com";
        if(userField.getText().isBlank())
        {
            AlertDialog.show("Username Error","Please enter username inorder to receive password reset code",AlertDialog.AlertType.ERROR);
            return;
        }
        else
        {
            List<List<String>> result = Util.executeSQLQuery("SELECT EMAIL FROM ADMINS WHERE NAME = \"%s\"".formatted(userField.getText()),null);
            if(!result.isEmpty())
            {
                List<String> rowData = result.get(0);
                receiverMail = rowData.get(0);
                System.out.println(receiverMail);
                Util.generateSecurityPin();

                //Send Mail To Admin
                Task<Void> mailTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        boolean res = Util.sendMail("Password Reset Code",Util.getSecurityMailText(Util.getSecurityPin()),receiverMail);
                        if(!res)
                        {
                            AlertDialog.show("Email Error","There was an error while sending security code", AlertDialog.AlertType.ERROR);
                            forgotPassBt.setDisable(false);
                        }
                        return null;
                    }
                };
                mailTask.setOnSucceeded(e -> {
                    Util.setSystemUser(userField.getText());
                    SceneManager.switchView("Views/ResetPasswordView.fxml");
                });
                new Thread(mailTask).start();

                //Aftermaths
                forgotPassBt.setDisable(true);
                Image loadGif = new Image(getClass().getResourceAsStream("Assets/Icons/loading.gif"));
                ImageView gifView = new ImageView(loadGif);
                gifView.setFitWidth(32);
                gifView.setFitHeight(32);
                forgotPassBt.setGraphic(gifView);
            }
            else
            {
                AlertDialog.show("Database Error","User is not registered in our database", AlertDialog.AlertType.ERROR);
            }
        }
    }
}
