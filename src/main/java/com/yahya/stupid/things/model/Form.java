package com.yahya.stupid.things.model;

import java.awt.*;

public class Form {

    private double slope;
    private double yIntercept;

    private final double originalSlope;
    private final double originalYIntercept;
    public Form(double slope, double yIntercept) {
        this.slope = this.originalSlope = slope;
        this.yIntercept = this.originalYIntercept = yIntercept;
    }

    public void reset() {
        setSlope(originalSlope);
        setyIntercept(originalYIntercept);
    }

    public double calculate(double x) {
        return slope*x + yIntercept;
    }

    public double getSlope() {
        return slope;
    }

    public double getYIntercept() {
        return yIntercept;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public void setyIntercept(double yIntercept) {
        this.yIntercept = yIntercept;
    }

    public void rotate90(Point myPoint) {
//        yIntercept = yIntercept/slope;
        slope = -1.0/slope;
        yIntercept = myPoint.y - slope* myPoint.x;
    }

    @Override
    public String toString() {
        return "y = " + slope + "x " + (yIntercept > 0 ? "+ " : "- ") + Math.abs(yIntercept);
    }
}
