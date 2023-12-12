package Geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Circle extends Geo<Circle> {
    protected Point center;
    protected double r;
    private Color borderColor = Color.black;

    public Circle(Point point, double radius) {
        this.center = point;
        this.r = radius;
    }

    public Circle clone() {
        Circle circle = new Circle(center.clone(), r);
        circle.setBorderColor(borderColor);
        circle.setColor(color);
        circle.setWidth(width);
        return circle;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawOval((int) Math.round(center.x - r) + x, (int) Math.round(center.y - r) + y, (int) Math.round(2 * r),
                (int) Math.round(2 * r));
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.fillOval((int) Math.round(center.x - r) + x, (int) Math.round(center.y - r) + y, (int) Math.round(2 * r),
                (int) Math.round(2 * r));
    }

    protected void moveX(double x) {
        center.moveX(x);
    }

    protected void moveY(double y) {
        center.moveY(y);
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    protected boolean intersects(Line line) {
        Point d = new Point(line.p1.x - line.p2.x, line.p1.y - line.p2.y);
        Point f = new Point(line.p2.x - center.x, line.p2.y - center.y);

        double a = d.x * d.x + d.y * d.y;
        double b = 2 * (f.x * d.x + f.y * d.y);
        double c = f.x * f.x + f.y * f.y - r * r;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return false;
        }
        discriminant = Math.sqrt(discriminant);
        double t1 = (-b - discriminant) / (2 * a);
        double t2 = (-b + discriminant) / (2 * a);
        if (t1 >= 0 && (t1 <= 1 || t2 <= 1)) {
            return true;
        }
        if (t2 >= 0 && t2 <= 1) {
            return true;
        }
        return false;
    }

    protected boolean isInside(Line line) {
        Point d = new Point(line.p1.x - line.p2.x, line.p1.y - line.p2.y);
        Point f = new Point(line.p2.x - center.x, line.p2.y - center.y);

        double a = d.x * d.x + d.y * d.y;
        double b = 2 * (f.x * d.x + f.y * d.y);
        double c = f.x * f.x + f.y * f.y - r * r;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return false;
        }
        discriminant = Math.sqrt(discriminant);
        double t1 = (-b - discriminant) / (2 * a);
        double t2 = (-b + discriminant) / (2 * a);
        if ((t1 >= 0 && (t1 <= 1 || t2 <= 1)) || (t1 < 0 && t2 > 1)) {
            return true;
        }
        return false;
    }
}
