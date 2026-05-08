package com.dsa.modernlibrarysystem;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;


public class DashAboutController
{
    @FXML
    private VBox root;
    @FXML
    private Hyperlink projectLabel;

    //Effects
    private DropShadow dropShadowEffect;

    //Transitions
    private TranslateTransition tt;
    private DropShadowColorTransition ct;
    private ScaleTransition st;

    @FXML
    public void initialize()
    {
        //Translate Transition
        Platform.runLater(() -> {
            dropShadowEffect = (DropShadow) root.getEffect();
            ct = new DropShadowColorTransition(Duration.seconds(0.5),dropShadowEffect,Color.rgb(0,0,0,0.8));
            ct.setInterpolator(Interpolator.EASE_BOTH);
        });

         tt = new TranslateTransition(Duration.seconds(0.5),root);
         tt.setInterpolator(Interpolator.EASE_BOTH);

         st = new ScaleTransition(Duration.seconds(0.5),root);

        root.setOnMouseEntered(e -> {
            st.setToX(1.001);
            st.setToY(1.001);
            tt.setToY(5);
            ct.setEndColorTo(Color.rgb(59, 130, 246, 0.2));
            st.playFromStart();
            tt.playFromStart();
            ct.playFromStart();
        });
        root.setOnMouseExited(e -> {
            st.setToX(1);
            st.setToY(1);
            tt.setToY(0);
            ct.setEndColorTo(Color.rgb(0,0,0,0.8));
            st.playFromStart();
            tt.playFromStart();
            ct.playFromStart();
        });


    }

    @FXML
    protected void openProjectURL()
    {
        try
        {
            String githubRepo = "https://github.com/PremKumarMishra/ModernLibrarySystem";
            ProcessBuilder pb = new ProcessBuilder("cmd","/c","start",githubRepo);
            pb.start();

        }
        catch (IOException e)
        {
            AlertDialog.show("Win32 Error","Failed to open link: " + e.getMessage(), AlertDialog.AlertType.ERROR);
        }

    }
}
