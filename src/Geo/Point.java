package Geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Point extends Geo<Point> {
    protected double x;
    protected double y;

    public Point(int x, int y) {
        this((double) x, (double) y);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected Point() {

    }

    protected void set(int x, int y) {
        set((double) x, (double) y);
    }

    protected void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point clone() {
        Point point = new Point(x, y);
        point.setBorderColor(color);
        point.setWidth(width);
        return point;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(this.x) + x,
                (int) Math.round(this.y) + y,
                (int) Math.round(this.x) + x,
                (int) Math.round(this.y) + y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    protected void setX(double x) {
        this.x = x;
    }

    protected void setY(double y) {
        this.y = y;
    }

    protected void moveX(double x) {
        this.x += x;
    }

    protected void moveY(double y) {
        this.y += y;
    }

    public void directionMove(int distance, double direction) {
        super.directionMove(distance, direction);
    }

    public void directionMove(double distance, double direction) {
        super.directionMove(distance, direction);
    }

    public void setBorderColor(Color color) {
        this.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    protected void rotate(double radian, Point pivot) {
        double s = Math.sin(radian);
        double c = Math.cos(radian);
        x -= pivot.x;
        y -= pivot.y;
        double xnew = x * c - y * s;
        double ynew = x * s + y * c;
        x = xnew + pivot.x;
        y = ynew + pivot.y;
    }

    protected Point getRotation(double radian, Point pivot) {
        Point point = clone();
        point.rotate(radian, pivot);
        return point;
    }
}
