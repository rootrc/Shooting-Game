package Geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Line extends Geo<Line> {
    final private double doublePrecision = 1000000;
    protected Point p1;
    protected Point p2;
    private double length;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        length = caculateLength();
    }

    protected Line() {
        p1 = new Point();
        p2 = new Point();
    }

    protected void set(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        length = caculateLength();
    }

    public Line clone() {
        Line line = new Line(p1.clone(), p2.clone());
        line.setBorderColor(color);
        line.setWidth(width);
        return line;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(p1.x) + x,
                (int) Math.round(p1.y) + y,
                (int) Math.round(p2.x) + x,
                (int) Math.round(p2.y) + y);
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public double getLength() {
        return length;
    }

    public void setP1(Point point) {
        p1 = point;
    }

    public void setP2(Point point) {
        p2 = point;
    }

    protected void moveX(double x) {
        p1.moveX(x);
        p2.moveX(x);
    }

    protected void moveY(double y) {
        p1.moveY(y);
        p2.moveY(y);
    }

    protected void setBorderColor(Color color) {
        this.color = color;
        p1.setBorderColor(color);
        p2.setBorderColor(color);
    }

    protected void setWidth(int width) {
        this.width = width;
        p1.setWidth(width);
        p1.setWidth(width);
    }

    protected boolean intersects(Line line) {
        double d = (p1.x - p2.x) * (line.p1.y - line.p2.y)
                - (p1.y - p2.y) * (line.p1.x - line.p2.x);
        if (d == 0) {
            return false;
        }
        double pre = (p1.x * p2.y - p1.y * p2.x),
                post = (line.p1.x * line.p2.y - line.p1.y * line.p2.x);
        double x = Math
                .round((pre * (line.p1.x - line.p2.x) - (p1.x - p2.x) * post) / d * doublePrecision)
                / doublePrecision;
        double y = Math
                .round((pre * (line.p1.y - line.p2.y) - (p1.y - p2.y) * post) / d * doublePrecision)
                / doublePrecision;
        if (x < Math.min(p1.x, p2.x) - 1 / doublePrecision
                || x > Math.max(p1.x, p2.x + 1 / doublePrecision)
                || x < Math.min(line.p1.x, line.p2.x) - 1 / doublePrecision
                || x > Math.max(line.p1.x, line.p2.x + 1 / doublePrecision)) {
            return false;
        }
        if (y < Math.min(p1.y, p2.y) - 1 / doublePrecision
                || y > Math.max(p1.y, p2.y + 1 / doublePrecision)
                || y < Math.min(line.p1.y, line.p2.y) - 1 / doublePrecision
                || y > Math.max(line.p1.y, line.p2.y + 1 / doublePrecision)) {
            return false;
        }

        return true;
    }

    protected Point intersectionPoint(Line line) {
        double d = (p1.x - p2.x) * (line.p1.y - line.p2.y)
                - (p1.y - p2.y) * (line.p1.x - line.p2.x);
        if (d == 0) {
            return null;
        }
        double pre = (p1.x * p2.y - p1.y * p2.x),
                post = (line.p1.x * line.p2.y - line.p1.y * line.p2.x);
        double x = Math
                .round((pre * (line.p1.x - line.p2.x) - (p1.x - p2.x) * post) / d * doublePrecision)
                / doublePrecision;
        double y = Math
                .round((pre * (line.p1.y - line.p2.y) - (p1.y - p2.y) * post) / d * doublePrecision)
                / doublePrecision;
        if (x < Math.min(p1.x, p2.x) - 1 / doublePrecision
                || x > Math.max(p1.x, p2.x + 1 / doublePrecision)
                || x < Math.min(line.p1.x, line.p2.x) - 1 / doublePrecision
                || x > Math.max(line.p1.x, line.p2.x + 1 / doublePrecision)) {
            return null;
        }
        if (y < Math.min(p1.y, p2.y) - 1 / doublePrecision
                || y > Math.max(p1.y, p2.y + 1 / doublePrecision)
                || y < Math.min(line.p1.y, line.p2.y) - 1 / doublePrecision
                || y > Math.max(line.p1.y, line.p2.y + 1 / doublePrecision)) {
            return null;
        }
        return new Point(x, y);
    }

    private double caculateLength() {
        return Math.sqrt(
                (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public double caculateRadian() {
        return -Math.atan2(p1.y - p2.y, p1.x - p2.x);
    }

}