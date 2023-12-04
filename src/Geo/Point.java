package Geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Point {
    public double x;
    public double y;
    Color color = Color.black;
    int width = 1;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(this.x), (int) Math.round(this.y), (int) Math.round(this.x),
                (int) Math.round(this.y));
    }

    public Point clone() {
        return new Point(x, y);
    }

    public Point translate(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point translate(double x, double y) {
        return new Point(this.x + x, this.y + y);
    }

    public void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    public void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    public void moveX(int x) {
        this.x += x;
    }

    public void moveX(double x) {
        this.x += x;
    }

    public void moveY(int y) {
        this.y += y;
    }

    public void moveY(double y) {
        this.y += y;
    }

    public void rotate(double radian, Point pivot) {
        double s = Math.sin(radian);
        double c = Math.cos(radian);
        x -= pivot.x;
        y -= pivot.y;
        double xnew = x * c - y * s;
        double ynew = x * s + y * c;
        x = xnew + pivot.x;
        y = ynew + pivot.y;
    }
    public Point getRotation(double radian, Point pivot) {
        double s = Math.sin(radian);
        double c = Math.cos(radian);
        double x = this.x;
        double y = this.y;
        x -= pivot.x;
        y -= pivot.y;
        double xnew = x * c - y * s;
        double ynew = x * s + y * c;
        x = xnew + pivot.x;
        y = ynew + pivot.y;
        return new Point(x, y);
    }
}
