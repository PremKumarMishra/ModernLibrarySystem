package com.dsa.modernlibrarysystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertDialog
{
    public enum AlertType {ERROR,CONFIRMATION,WARNING,INFORMATION};
    private static final Rectangle2D screenSize = Screen.getPrimary().getBounds();

    public static boolean show(String title,String content,AlertType type)
    {
        final boolean result[] = {false};
        final double offSet[] = {0,0};

        //Dialog Stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setMinWidth(147);
        stage.setMinHeight(170);
        stage.setTitle(title);

        //Dialog Body
        VBox body = new VBox(10);
        body.setAlignment(Pos.TOP_CENTER);
        body.getStyleClass().add("alert-dialog");

        //Dialog Title Bar
        HBox titleBox = new HBox(5);
        titleBox.getStyleClass().add("alert-title-box");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");
        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);
        Button titleButton = new Button();
        titleButton.getStyleClass().add("alert-button-close");
        titleButton.setPrefSize(15,15);
        titleButton.setOnAction(e -> {stage.close();});
        titleBox.getChildren().setAll(titleLabel,titleSpacer,titleButton);

        //Dialog Content Box
        VBox contentBox = new VBox(10);
        contentBox.getStyleClass().add("alert-content");
        contentBox.setAlignment(Pos.TOP_CENTER);

        //Dialog Content
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("alert-message");

        //Dialog Content Style Class
        switch(type)
        {
            case ERROR -> contentLabel.getStyleClass().add("msg-error");
            case WARNING -> contentLabel.getStyleClass().add("msg-warning");
            case INFORMATION -> contentLabel.getStyleClass().add("msg-info");
            case CONFIRMATION -> contentLabel.getStyleClass().add("msg-confirm");
        }

        //Dialog Button Container Spacer
        Region btnSpacer = new Region();
        VBox.setVgrow(btnSpacer,Priority.ALWAYS);

        //Dialog Button Container
        HBox buttonContainer = new HBox(10);
        buttonContainer.getStyleClass().add("alert-button-container");
        buttonContainer.setAlignment(Pos.CENTER);

        Button actionBtn = new Button(type == AlertType.CONFIRMATION ? "Yes" : "Ok");
        actionBtn.getStyleClass().add("alert-button-primary");
        actionBtn.setOnAction(e -> {result[0] = true;stage.close();});

        if (type == AlertType.CONFIRMATION)
        {
            Button cancelBtn = new Button("No");
            cancelBtn.getStyleClass().add("alert-button-secondary");
            cancelBtn.setOnAction(e -> {stage.close();});
            buttonContainer.getChildren().add(cancelBtn);
        }

        buttonContainer.getChildren().add(actionBtn);

        //Dialog Bind Title Bar
        titleBox.setOnMousePressed(e -> {
            offSet[0] = e.getScreenX() - stage.getX();
            offSet[1] = e.getScreenY() - stage.getY();

        });

        titleBox.setOnMouseDragged(e ->{
            stage.setX(Math.min(Math.max(0,e.getScreenX() - offSet[0]),screenSize.getWidth() - stage.getWidth()));
            stage.setY(Math.min(Math.max(0,e.getScreenY() - offSet[1]),screenSize.getHeight()-stage.getHeight()));
        });

        //Add Nodes To Body
        contentBox.getChildren().setAll(contentLabel,btnSpacer,buttonContainer);
        body.getChildren().setAll(titleBox,contentBox);

        //Dialog Scene
        Scene scene = new Scene(body);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().setAll(AlertDialog.class.getResource("Stylesheets/AlertDialog.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();

        return result[0];
    }
}
