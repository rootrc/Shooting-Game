package Geo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Polygon extends Geo<Polygon> {
    private int length;
    private Point[] points;
    private Line[] lines;
    private Point centroid = new Point();
    public double size;

    public Polygon(Point[] points) {
        this.length = points.length;
        this.points = points.clone();
        lines = new Line[length];
        for (int i = 0; i < length; i++) {
            lines[i] = new Line(points[i], points[(i + 1) % length]);
        }
        computeCentroid();
        size = new Line(centroid, points[0]).getLength();
    }

    protected Polygon() {

    }

    protected void set(Point[] points) {
        this.length = points.length;
        this.points = points.clone();
        lines = new Line[length];
        for (int i = 0; i < length; i++) {
            lines[i] = new Line(points[i], points[(i + 1) % length]);
        }
        computeCentroid();
        size = new Line(centroid, points[0]).getLength();
    }

    public Polygon clone() {
        Polygon polygon = new Polygon(getPoints());
        polygon.setColor(color);
        polygon.setWidth(width);
        return polygon;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        for (int i = 0; i < length; i++) {
            lines[i].draw(g2d, x, y);
        }
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        int[] xPoints = new int[length];
        int[] yPoints = new int[length];
        for (int i = 0; i < length; i++) {
            xPoints[i] = (int) Math.round(points[i].x + x);
            yPoints[i] = (int) Math.round(points[i].y + y);
        }
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, length);
    }

    protected void moveX(double x) {
        for (Point point : points) {
            point.moveX(x);
        }
        centroid.moveX(x);
    }

    protected void moveY(double y) {
        for (Point point : points) {
            point.moveY(y);
        }
        centroid.moveY(y);
    }

    protected void setBorderColor(Color color) {
        for (Line line : lines) {
            line.setBorderColor(color);
        }
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    protected Color getColor() {
        return color;
    }

    protected void setWidth(int width) {
        for (Line line : lines) {
            line.setWidth(width);
        }
    }

    protected Point[] getPoints() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        return points;
    }

    public Point getPoint(int i) {
        return points[i].clone();
    }

    public boolean intersects(Polygon polygon) {
        for (Line line1 : lines) {
            for (Line line2 : polygon.lines) {
                if (line1.intersects(line2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean intersects(Line line) {
        for (Line line2 : lines) {
            if (line2.intersects(line)) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Circle circle) {
        for (Line line : lines) {
            if (circle.intersects(line)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInside(Circle circle) {
        for (Line line : lines) {
            if (circle.isInside(line)) {
                return true;
            }
        }
        return false;
    }

    protected void rotate(double radian, Point pivot) {
        for (Point point : points) {
            point.rotate(radian, pivot);
        }
    }

    protected void rotate(double radian) {
        rotate(radian, centroid);
    }

    protected Polygon getRotation(double radian) {
        Polygon polygon = clone();
        polygon.rotate(radian);
        return polygon;
    }

    private Point computeCentroid() {
        double signedArea = 0.0;
        double a = 0.0;
        for (Line line : lines) {
            a = line.p1.x * line.p2.y - line.p2.x * line.p1.y;
            signedArea += a;
            centroid.moveX((line.p1.x + line.p2.x) * a);
            centroid.moveY((line.p1.y + line.p2.y) * a);
        }
        signedArea /= 2;
        centroid.setX(centroid.x / (6.0 * signedArea));
        centroid.setY(centroid.y / (6.0 * signedArea));
        return centroid;
    }

    protected Point getCentroid() {
        return centroid;
    }
}
