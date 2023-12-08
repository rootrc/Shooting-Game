

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class Point {
    double x;
    double y;
    Color color = Color.black;
    int width = 1;
    
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(this.x) + Game.getInstance().panel.xAdjust, (int) Math.round(this.y) + Game.getInstance().panel.yAdjust, (int) Math.round(this.x) + Game.getInstance().panel.xAdjust,
                (int) Math.round(this.y) + Game.getInstance().panel.yAdjust);
    }

    public Point clone() {
        Point point = new Point(x, y);
        point.width = width;
        return point;
    }

    Point translate(int x, int y) {
        Point point = new Point(this.x + x, this.y + y);
        point.width = width;
        return point;
    }

    Point translate(double x, double y) {
        Point point = new Point(this.x + x, this.y + y);
        point.width = width;
        return point;
    }

    void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    void moveX(int x) {
        this.x += x;
    }

    void moveX(double x) {
        this.x += x;
    }

    void moveY(int y) {
        this.y += y;
    }

    void moveY(double y) {
        this.y += y;
    }

    void rotate(double radian, Point pivot) {
        double s = Math.sin(radian);
        double c = Math.cos(radian);
        x -= pivot.x;
        y -= pivot.y;
        double xnew = x * c - y * s;
        double ynew = x * s + y * c;
        x = xnew + pivot.x;
        y = ynew + pivot.y;
    }
    Point getRotation(double radian, Point pivot) {
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
