package com.dsa.modernlibrarysystem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SceneManager
{
    private static Pane root;
    private static Pane dashRoot;

    public static void setRoot(Pane r)
    {
        root = r;
        if (root instanceof VBox) {
            VBox.setVgrow(root, Priority.ALWAYS);
        }
        if (root instanceof HBox) {
            HBox.setHgrow(root, Priority.ALWAYS);
        }
    }

    public static void setDashBoardRoot(Pane r)
    {
        dashRoot = r;
    }

    public static void switchView(String viewFXML)
    {
        try
        {
            Parent view = FXMLLoader.load(SceneManager.class.getResource(viewFXML));
            root.getChildren().setAll(view);

            if (root instanceof VBox) {
                VBox.setVgrow(view, Priority.ALWAYS);
            }
            if (root instanceof HBox) {
                HBox.setHgrow(view, Priority.ALWAYS);
            }
        }
        catch (Exception e)
        {
            AlertDialog.show("FXML Error","Failed to load FXML file", AlertDialog.AlertType.ERROR);
        }
    }

    public static void switchDashBoardView(String viewFXML)
    {
        try
        {
            Parent view = FXMLLoader.load(SceneManager.class.getResource(viewFXML));
            dashRoot.getChildren().setAll(view);
        }

        catch (Exception e)
        {
            AlertDialog.show("FXML Error","Failed to load FXML file", AlertDialog.AlertType.ERROR);
        }
    }
}
