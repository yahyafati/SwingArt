package com.yahya.stupid.things.model;

import java.awt.*;

public class Planet implements Cloneable {
    final String name;
    final int radius;
    double currentDegree;
    final double realOrbitRadius;
    final double speed;
    Color color = Color.decode("#12af8a");

    private final static double MERCURY_REAL_RADIUS = 46e6;

    public Planet(String name, int radius, double realOrbitRadius, double speed) {
        this.name = name;
        this.radius = radius;
        this.realOrbitRadius = realOrbitRadius;
        this.speed = speed;
    }

    public double getCurrentDegree() {
        return currentDegree;
    }

    public void setCurrentDegree(double currentDegree) {
        this.currentDegree = currentDegree;
    }

    public void increaseDegree() {
        this.currentDegree = (getCurrentDegree() + speed)%360;
    }

    public int currentX() {
//        return (int) (getScaledOrbitRadius(scaledMercuryRadius) *Math.cos(Math.toRadians(currentDegree)));
        return (int) (getRealOrbitRadius() *Math.cos(Math.toRadians(currentDegree)));
    }

    public int currentY() {
//        return (int) (getScaledOrbitRadius(scaledMercuryRadius) *Math.sin(Math.toRadians(currentDegree)));
        return (int) (getRealOrbitRadius() *Math.sin(Math.toRadians(currentDegree)));
    }

    public double getRealOrbitRadius() {
        return realOrbitRadius;
    }

    public int getScaledOrbitRadius(int scaledMercuryRadius) {
        return (int) ((getRealOrbitRadius()/MERCURY_REAL_RADIUS) * scaledMercuryRadius);
    }

    public double getSpeed() {
        return speed;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public Planet clone() {
        try {
            Planet clone = (Planet) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
//            clone.color = Color.decode("#12af8a");
            clone.color = new Color((int) (Math.random()*Integer.MAX_VALUE));
//            clone.setCurrentDegree(Math.random()*360);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
