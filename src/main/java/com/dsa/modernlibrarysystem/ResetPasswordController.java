package com.dsa.modernlibrarysystem;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;


public class ResetPasswordController
{
    private Timer otpResetTimer = new Timer();
    @FXML
    protected TextField otpField;
    @FXML
    protected TextField passwordField;
    @FXML
    protected TextField confirmPasswordField;
    @FXML
    protected Button resendBt;

    @FXML
    public void initialize()
    {
        startResetTimer();
    }

    @FXML
    protected void resetPassword()
    {
        try
        {
            if (Integer.parseInt(otpField.getText()) == Util.getSecurityPin())
            {
                if(passwordField.getText().length() >= 6)
                {
                    if (passwordField.getText().equals(confirmPasswordField.getText()))
                    {
                        String passwordHash = Util.getSha256Hash(passwordField.getText());
                        if(passwordHash.isBlank())
                        {
                            AlertDialog.show("Hashing Error","Failed to generate password hash",AlertDialog.AlertType.ERROR);
                        }
                        else
                        {
                            System.out.printf("UPDATE ADMINS SET PASSWORD = \"%s\" WHERE NAME = \"%s\";%n", passwordHash,Util.getSystemUser());
                            Util.updateSQLQuery("UPDATE ADMINS SET PASSWORD = \"%s\" WHERE NAME = \"%s\";".formatted(passwordHash,Util.getSystemUser()));
                            AlertDialog.show("Reset Successful","User password is successfully reset, you may now login to your account",AlertDialog.AlertType.INFORMATION);
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
//                System.out.println(Util.getSecurityPin());
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e)
        {
//            System.out.println(Util.getSecurityPin());
            AlertDialog.show("Security Pin Error","Please enter correct security pin",AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void resendCode()
    {
        List<List<String>> result = Util.executeSQLQuery("SELECT EMAIL FROM ADMINS WHERE NAME=\"%s\";".formatted(Util.getSystemUser()),null);
        if (!result.isEmpty())
        {
            List<String> rowData = result.get(0);
            String receiverMail = rowData.get(0);
            Task<Void> mailTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    boolean res = Util.sendMail("Password Reset Code",Util.getSecurityMailText(Util.getSecurityPin()),receiverMail);
                    if(!res)
                    {
                        AlertDialog.show("Email Error","There was an error while sending security code",AlertDialog.AlertType.ERROR);
                    }
                    return null;
                }
            };
            mailTask.setOnSucceeded(e -> {
                Image icon = new Image(getClass().getResourceAsStream("Assets/Icons/resend_ico.png"));
                ImageView iconView = new ImageView(icon);
                resendBt.setGraphic(iconView);
            });
            new Thread(mailTask).start();

            //Aftermaths
            Image loadImg = new Image(getClass().getResourceAsStream("Assets/Icons/loading.gif"));
            ImageView loadView = new ImageView(loadImg);
            loadView.setFitHeight(32);
            loadView.setFitWidth(32);
            resendBt.setGraphic(loadView);

            resendBt.setDisable(true);
            startResetTimer();

        }
        else
        {
            AlertDialog.show("Fetch Error","Failed to fetch admin email from database",AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void returnToLogin()
    {
        SceneManager.switchView("Views/LoginView.fxml");
    }

    protected void startResetTimer()
    {
        otpResetTimer.scheduleAtFixedRate(new TimerTask() {
            private int timer = 60;
            @Override
            public void run()
            {
                if (timer <=0)
                {
                    Util.generateSecurityPin();
                    Platform.runLater(() -> {
                        resendBt.setDisable(false);
                        resendBt.setText("Resend");
                    });
                    cancel();
                }
                else
                {
                    timer--;
                    Platform.runLater(() -> {
                        resendBt.setText("00:%02d".formatted(timer));
                    });
                }


            }
        }, 0, 1000);
    }
}
