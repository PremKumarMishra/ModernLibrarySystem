package com.dsa.modernlibrarysystem;

import javafx.animation.Transition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class DropShadowColorTransition extends Transition
{
    private Color startColor;
    private Color endColor;
    private DropShadow dropShadow;

    public DropShadowColorTransition(Duration duration, DropShadow dropShadow, Color startColor)
    {
        this.dropShadow = dropShadow;
        this.startColor = endColor = startColor;
        this.setCycleDuration(duration);
    }

    public void setEndColorTo(Color color)
    {
        startColor = endColor;
        endColor = color;
    }

    @Override
    public void interpolate(double frac)
    {
        Color current = startColor.interpolate(endColor,frac);
        dropShadow.setColor(current);
    }

}
