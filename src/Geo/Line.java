package Geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Line {
    Point p1;
    Point p2;
    Color color = Color.black;
    int width = 1;
    final double doublePrecision = 1000000;
    public double length;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        length = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public void setBorderColor(Color color) {
        this.color = color;
        p1.color = color;
        p2.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
        p1.width = width;
        p2.width = width;
    }

    public void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    public Line clone() {
        return new Line(p1.clone(), p2.clone());
    }

    public Line translate(int x, int y) {
        return new Line(p1.translate(x, y), p2.translate(x, y));
    }

    public Line translate(double x, double y) {
        return new Line(p1.translate(x, y), p2.translate(x, y));
    }

    public void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    public void moveX(int x) {
        p1.moveX(x);
        p2.moveX(x);
    }

    public void moveX(double x) {
        p1.moveX(x);
        p2.moveX(x);
    }

    public void moveY(int y) {
        p1.moveY(y);
        p2.moveY(y);
    }

    public void moveY(double y) {
        p1.moveY(y);
        p2.moveY(y);
    }

    public boolean intersects(Line line) {
        double d = (p1.x - p2.x) * (line.p1.y - line.p2.y) - (p1.y - p2.y) * (line.p1.x - line.p2.x);
        if (d == 0) {
            return false;
        }
        double pre = (p1.x * p2.y - p1.y * p2.x), post = (line.p1.x * line.p2.y - line.p1.y * line.p2.x);
        double x = Math.round((pre * (line.p1.x - line.p2.x) - (p1.x - p2.x) * post) / d * doublePrecision)
                / doublePrecision;
        double y = Math.round((pre * (line.p1.y - line.p2.y) - (p1.y - p2.y) * post) / d * doublePrecision)
                / doublePrecision;
        if (x < Math.min(p1.x, p2.x) - 1 / doublePrecision || x > Math.max(p1.x, p2.x + 1 / doublePrecision)
                || x < Math.min(line.p1.x, line.p2.x) - 1 / doublePrecision
                || x > Math.max(line.p1.x, line.p2.x + 1 / doublePrecision)) {
            return false;
        }
        if (y < Math.min(p1.y, p2.y) - 1 / doublePrecision || y > Math.max(p1.y, p2.y + 1 / doublePrecision)
                || y < Math.min(line.p1.y, line.p2.y) - 1 / doublePrecision
                || y > Math.max(line.p1.y, line.p2.y + 1 / doublePrecision)) {
            return false;
        }
        // System.out.print(x + " ");
        // System.out.println(y);
        // System.out.print(this.p1.x + " ");
        // System.out.println(this.p1.y);
        // System.out.print(this.p2.x + " ");
        // System.out.println(this.p2.y);
        // System.out.print(line.p1.x + " ");
        // System.out.println(line.p1.y);
        // System.out.print(line.p2.x + " ");
        // System.out.println(line.p2.y);
        // System.out.println();

        return true;
    }

    public Point intersectionPoint(Line line) {
        double d = (p1.x - p2.x) * (line.p1.y - line.p2.y) - (p1.y - p2.y) * (line.p1.x - line.p2.x);
        if (d == 0) {
            return null;
        }
        double pre = (p1.x * p2.y - p1.y * p2.x), post = (line.p1.x * line.p2.y - line.p1.y * line.p2.x);
        double x = Math.round((pre * (line.p1.x - line.p2.x) - (p1.x - p2.x) * post) / d * doublePrecision)
                / doublePrecision;
        double y = Math.round((pre * (line.p1.y - line.p2.y) - (p1.y - p2.y) * post) / d * doublePrecision)
                / doublePrecision;
        if (x < Math.min(p1.x, p2.x) - 1 / doublePrecision || x > Math.max(p1.x, p2.x + 1 / doublePrecision)
                || x < Math.min(line.p1.x, line.p2.x) - 1 / doublePrecision
                || x > Math.max(line.p1.x, line.p2.x + 1 / doublePrecision)) {
            return null;
        }
        if (y < Math.min(p1.y, p2.y) - 1 / doublePrecision || y > Math.max(p1.y, p2.y + 1 / doublePrecision)
                || y < Math.min(line.p1.y, line.p2.y) - 1 / doublePrecision
                || y > Math.max(line.p1.y, line.p2.y + 1 / doublePrecision)) {
            return null;
        }
        return new Point(x, y);
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x),
                (int) Math.round(p2.y));
    }

    public double caculateRadian() {
        return -Math.atan2(p1.y - p2.y, p1.x - p2.x);
    }

}
