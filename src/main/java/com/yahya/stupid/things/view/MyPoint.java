package com.yahya.stupid.things.view;

public class MyPoint {
    public double x, y;

    MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    MyPoint(MyPoint myPoint) {
        this(myPoint.x, myPoint.y);
    }

    void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}