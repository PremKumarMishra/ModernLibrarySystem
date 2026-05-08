package com.dsa.modernlibrarysystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController
{
    @FXML
    private VBox bodyArea;

    @FXML
    private Label welcome_text;

    @FXML
    public void initialize()
    {
        welcome_text.setText("Welcome, " + Util.getSystemUser());
        SceneManager.setDashBoardRoot(bodyArea);
        SceneManager.switchDashBoardView("Views/DashHomeView.fxml");
    }

    @FXML
    protected void switchViewToBooks()
    {
        SceneManager.switchDashBoardView("Views/DashBookView.fxml");
    }

    @FXML
    protected void switchViewToMembers()
    {
        SceneManager.switchDashBoardView("Views/DashMembersView.fxml");
    }

    @FXML
    protected void switchViewToIssueReturn()
    {
        SceneManager.switchDashBoardView("Views/DashIssueReturnView.fxml");
    }

    @FXML
    protected void switchViewToLibrarians()
    {
        SceneManager.switchDashBoardView("Views/DashLibrarianView.fxml");
    }

    @FXML
    protected void switchViewToReports()
    {
        SceneManager.switchDashBoardView("Views/DashReportView.fxml");
    }

    @FXML
    protected void switchViewToAbout() {SceneManager.switchDashBoardView("Views/DashAboutView.fxml");}

    @FXML
    protected void logOutFromSystem()
    {
        boolean res = AlertDialog.show("Logout Confirmation","Are you sure you want to log out ?",AlertDialog.AlertType.CONFIRMATION);
        if(res)
        {
            SceneManager.switchView("Views/LoginView.fxml");
        }
    }
}
